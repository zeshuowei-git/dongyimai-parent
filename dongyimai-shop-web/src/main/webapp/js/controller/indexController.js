
//首页控制器
app.controller("indexController",function ($scope,loginService) {


    //获取用户名
    $scope.ShowLoginName=function(){

        loginService.loginName().success(function (response) {


            $scope.loginName=response.loginName;


        })


    }


})