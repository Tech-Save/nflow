(function () {
  'use strict';

  var m = angular.module('nflowExplorer.search.criteriaModel', []);

  m.factory('CriteriaModel', function() {
    var self  = {};
    self.model = {};
    self.initialize = initialize;
    self.toQuery = toQuery;
    self.isEmpty = isEmpty;
    self.onDefinitionChange = onDefinitionChange;

    return self;

    function initialize(initValues, definitions) {
      angular.copy({}, self.model);

      self.model.definition = ensureTypeInDefinitions(initValues.type, definitions);
      self.model.state = ensureStateIdInDefinitionStates(initValues.stateId, self.model.definition);
      self.model.parentWorkflowId = nonValueToNull(initValues.parentWorkflowId);
    }

    function toQuery() {
      var q = {};

      q.type = _.result(self.model.definition, 'type');
      q.state = _.result(self.model.state, 'id');
      _.defaults(q, _.omit(self.model, ['definition', 'state']));
      return omitNonValues(q);
    }

    function isEmpty() {
      return _.isEmpty(omitNonValues(self.model));
    }

    function onDefinitionChange() {
      self.model.state = ensureStateIdInDefinitionStates(_.result(self.model.state, 'id'), self.model.definition);
    }

    function ensureTypeInDefinitions(type, definitions) {
      return nonValueToNull(_.find(definitions, function (d) { return d.type === type; }));
    }

    function ensureStateIdInDefinitionStates(stateId, definition) {
      return definition ? nonValueToNull(_.find(definition.states, function (s) { return s.id === stateId; })) : null;
    }

    function omitNonValues(object) {
      return _.omitBy(object, function (v) { return _.isUndefined(v) || _.isNull(v); });
    }

    function nonValueToNull(v) {
      return v || null;
    }
  });
})();
