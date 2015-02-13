(function () {
  'use strict';

  var m = angular.module('nflowVisApp.frontPage.definitionList', []);

  m.directive('definitionList', function() {
    return {
      restrict: 'E',
      replace: true,
      scope: {
        definitions: '='
      },
      bindToController: true,
      controller: 'DefinitionListCtrl as ctrl',
      templateUrl: 'app/front-page/definitionList.html'
    };
  });

  m.controller('DefinitionListCtrl', function() {});

})();
