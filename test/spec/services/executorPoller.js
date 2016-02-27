'use strict';

describe('Service: ExecutorPoller', function () {
  var $httpBackend,
    $interval,
    ExecutorPoller,
    url,
    pollInterval;

  beforeEach(module('nflowExplorer.services.ExecutorPoller'));
  beforeEach(inject(function (_$httpBackend_, _$interval_, _ExecutorPoller_, config) {
    $httpBackend = _$httpBackend_;
    $interval = _$interval_;
    ExecutorPoller = _ExecutorPoller_;

    url = config.nflowUrl + '/v1/workflow-executor';
    pollInterval = config.radiator.pollPeriod * 1000;
  }));

  afterEach(function() {
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  });

  it('initially has no executors', function () {
    expect(ExecutorPoller.executors).toEqual([]);
  });

  it('when started, executors are set and poll is started', function () {
    var backend = $httpBackend.whenGET(url).respond(200, [ 'expected' ]);

    // important: reference to executors must not be overwritten on poll update
    var actual = ExecutorPoller.executors;
    expect(actual).toEqual([]);

    $httpBackend.expectGET(url);
    ExecutorPoller.start();
    $httpBackend.flush();
    expect(actual).toEqual([ 'expected']);

    backend.respond(200, [ 'expected 2' ]);
    $httpBackend.expectGET(url);
    $interval.flush(pollInterval);
    $httpBackend.flush();
    expect(actual).toEqual([ 'expected 2']);

    backend.respond(200, [ 'expected 3' ]);
    $httpBackend.expectGET(url);
    $interval.flush(pollInterval);
    $httpBackend.flush();
    expect(actual).toEqual([ 'expected 3']);
  });

  it('after start subsequent calls to start do not start multiple pollers', inject(function (Executors) {
    var spy = sinon.spy(Executors, 'query');
    $httpBackend.whenGET(url).respond(200, []);

    ExecutorPoller.start();
    ExecutorPoller.start();
    $httpBackend.flush();

    expect(spy.callCount).toBe(1);

    Executors.query.restore();
  }));
});
