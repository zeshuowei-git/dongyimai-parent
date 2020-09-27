app.controller("contentController",function ($scope,contentService) {


    //定义广告列表
    $scope.contentList=[];


    //定义跳转方法
    $scope.search=function(){

        location.href="http://localhost:9104/search.html#?keywords="+$scope.searchMap.keywords;


    }




    //根据类目查询广告列表
    $scope.findByCategoryId=function(categoryId){

        contentService.findByCategoryId(categoryId).success(
            function(response){

                //数据的处理
                $scope.contentList[categoryId]=response;

            }
        )


    }


})