var appModule = angular.module('app' , ['ngRoute', 'messagingModule-rs', 'messagingModule-ws']);

appModule.config(function($routeProvider) {
    $routeProvider
        .when('/rs', {
            templateUrl : 'Messaging/messaging.html',
            controller  : 'messageController-rs'
        })
        .when('/ws', {
            templateUrl : 'Messaging/messaging.html',
            controller  : 'messageController-ws'
        })
});

appModule.controller('appController', ['$scope', '$window', function($scope, $window) {
	$scope.setController = function(controller) {
		if(controller == 'Rest') {
			$window.location.href = "#!/rs";
		}
		else if(controller == 'Websocket') {
			$window.location.href = "#!/ws";
		}
	}
}]);