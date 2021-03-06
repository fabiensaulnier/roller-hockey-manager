'use strict';

/* Services */

var zoneServices = angular.module('zoneServices', ['ngResource']);

zoneServices.factory('Phone', ['$resource',
  function($resource) {
    return $resource('phones/:phoneId.json', {}, {
      query: {
        method: 'GET',
        params: {
          phoneId: 'phones'
        },
        isArray: true
      }
    });
  }
]);
