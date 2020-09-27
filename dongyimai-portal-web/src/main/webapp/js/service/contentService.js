app.service("contentService",function ($http) {

    //根据广告类目查询广告列表
    this.findByCategoryId=function (categoryId) {

        return $http.get("content/findByContenCategoryId.do?categoryId="+categoryId);
    }



})