var app = angular.module("app", ["ngJsTree"]);

app.controller("jsTreeController", function ($scope) {

	$scope.init = function () {
		$scope.model = $scope.model || {};
		$scope.model.jsTreeData = [
			{ "id": "ajson1", "parent": "#", "text": "Simple root node" },
			{ "id": "ajson2", "parent": "#", "text": "Root node 2" },
			{ "id": "ajson3", "parent": "ajson2", "text": "Child 1" },
			{ "id": "ajson4", "parent": "ajson2", "text": "Child 2" },
		];
		
		$scope.treeConfig = {
			plugins:["checkbox"]
		};
		
		$scope.tree = {};
		$scope.tree.treeInstance = {};
		
		$scope.checkArray = [];
	}
	
	$scope.updateCheckArray = function(){
		$scope.checkArray = $scope.tree.treeInstance.jstree(true).get_selected();
	}

	$scope.init();
})