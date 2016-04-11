/**
 * Created by Payne on 4/9/16.
 */
var app = angular.module('todo', ['ionic']);

app.controller('TodoCtrl',function ($scope,$ionicModal,$timeout,Projects,$ionicSideMenuDelegate) {

    var createProject = function (projectTitle) {
        var newProject = Projects.newProject(projectTitle);
        $scope.projects.push(newProject);
        Projects.save($scope.projects);
        $scope.selectProject(newProject,$scope.projects.length-1);
    }

    $scope.projects = Projects.all();

    $scope.activeProject = $scope.projects[Projects.getLastActiveIndex()];

    $scope.newProject = function () {
        var projectTitle = prompt('Project name');
        if (projectTitle){
            createProject(projectTitle);
        }
    };

    $scope.selectProject = function (project, index) {
        $scope.activeProject = project;
        Projects.setLastActiveIndex(index);
        $ionicSideMenuDelegate.toggleLeft(false); //??
    }
    
    // 创建并载入模板
    $ionicModal.fromTemplateUrl('new-task.html',function (modal) {
        $scope.taskModal = modal;
    },{
        scope: $scope,
        animation: 'slide-in-up'
    });

    // 提交表单时,创建任务
    $scope.createTask = function (task) {

        if (!$scope.activeProject || !task){
            return;
        }
        $scope.activeProject.tasks.push({
            title: task.title
        });
        $scope.taskModal.hide();

        Projects.save($scope.projects);
        task.title = "";
    };

    //新建任务,打开新增任务表单
    $scope.newTask = function () {
        $scope.taskModal.show();
    };

    //关闭新增任务表单
    $scope.closeNewTask = function () {
        $scope.taskModal.hide();
    };

    //??
    $scope.toggleProjects = function () {
        $ionicSideMenuDelegate.toggleLeft(); //??
    };
    
    $timeout(function () {
        if ($scope.projects.length == 0){
            while (true){
                var  projectTitle = prompt("You first project title");
                if (projectTitle){
                    createProject(projectTitle);
                    break;
                }
            }
        }
    });
});

app.factory('Projects',function () {
    return {
        all: function () {
            var projectString = window.localStorage['projects'];
            if (projectString){
                return angular.fromJson(projectString);
            }
            return [];
        },
        save:function (projects) {
            window.localStorage['projects'] = angular.toJson(projects);
        },
        newProject: function (projectTitle) {
            //添加一个新工程
            return {
                title:projectTitle,
                tasks:[]
            }
        },

        getLastActiveIndex:function () {
            return parseInt(window.localStorage['lastActiveProject']) || 0;
        },

        setLastActiveIndex:function (index) {
            window.localStorage['lastActiveProject'] = index;
        }
    }
});
