//商品控制层
app.controller('goodsController', function ($scope, $controller,$location, uploadService,typeTemplateService, itemCatService, goodsService) {

    $controller('baseController', {$scope: $scope});//继承


    //判断规格的选中转态，进行回显
    $scope.checkAttributeValue=function(specName,optionName){
        /**
         * specName:规格名称  网络
         * optionName:规格选项名称 移动3G
         * 作用：
         *  获取遍历过程中的规格选项名称，去对比后台查询出来的已经选择的规格选项
         *
         *  "[
         *  {"attributeValue":["移动3G","移动4G"],"attributeName":"网络"},
         *  {"attributeValue":["16G","32G"],"attributeName":"机身内存"}
         *  ]"
         */

        //获取规格数组
        var items=$scope.entity.goodsDesc.specificationItems;


        //调用查询规格对象的方法
       var obj= searchObjectBykey(items,"attributeName",specName);

       //判断是否存在
        if(obj){//{"attributeValue":["16G","32G"],"attributeName":"机身内存"}

            //获取规格选项数组
            if(obj.attributeValue.indexOf(optionName)>=0){

                return true;
            }else{
                return false;
            }


        }else{


            return false;
        }






    }



    //修改页面跳转
    $scope.toEdit=function(id){

        //发送请求
        location.href="/admin/goods_edit.html#?id="+id;

    }



    //定义存储所有分类列表
    $scope.itemCatList=[];
    //查询所有分类
    $scope.findItemCatList=function(){

        itemCatService.findAll().success(
            function (response) {
                //List<ItemCat>
                for(var i=0;i<response.length;i++){
                    //response[i] itemCat

                    $scope.itemCatList[response[i].id]=response[i].name;

                }



            }
        )


    }



    //定义状态列表
    //0未审核 1审核通过 2驳回
    $scope.status=['未审核','审核通过','驳回'];


    //$scope.entity.itemList=[];

    //生成SKU列表
    $scope.createItemList=function(){

    //定义sku的数据格式
        /**
         *
         * spec:SKU的拼装对象
         * price:价格
         * num：库存
         *status：是否启用
         * isDefault：是否默认
         *
         *
         */
        //sku初始化
        $scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' } ];

        //获取规格[{"attributeName":"机身内存","attributeValue":["16G"]},{"attributeName":"网络","attributeValue":["移动3G"]}]
        var items=$scope.entity.goodsDesc.specificationItems;
        //遍历规格,将规格名称和规格选项拼接处理

        for(var i=0;i<items.length;i++){
            //items {"attributeName":"机身内存","attributeValue":["16G","32G"]}

            $scope.entity.itemList= addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
        }
    }


    /**
     * list：封装sku列表
     * columnName:规格名称
     * columnValues：规格选项数组
     *
     * @param list
     * @param columnName
     * @param columnValues
     */
    addColumn=function(list,columnName,columnValues){


        //定义数组，收集拼接后的sku
        var newList=[];

        //遍历sku列表
        for(var i=0;i<list.length;i++){

            var oldObj =list[i]; //{spec:{机身内存：16G},price:0,num:99999,status:'0',isDefault:'0' }
            //遍历选择的规格属性 ["16G","32G"]
            for(var j=0;j<columnValues.length;j++){
                //深克隆
                var newObj=JSON.parse(JSON.stringify(oldObj));
                //赋值
                newObj.spec[columnName]=columnValues[j];

                //复制记录
                newList.push(newObj);




            }


        }

        return newList;






    }



    //选择规格和规格选项
    /**
     *
     * @param $event 复选框的状态对象
     * @param name  规格名称 ，例如“网络”
     * @param value 规格选项名称， 例如“移动3G”
     *
     * 作用：
     *  封装成如下格式数据：
     *   [{“attributeName”:”规格名称”,”attributeValue”:[“规格选项1”,“规格选项2”.... ]  } , ....  ]
     *
     */

    //初始化提交数据实体
    $scope.entity = {goods: {}, goodsDesc: {itemImages: [],specificationItems:[]}};

    $scope.updateSpceAtribute=function($event,name,value){

        //尝试获取specificationItems中有没有指定的规格名称

        var obj=searchObjectBykey($scope.entity.goodsDesc.specificationItems,"attributeName",name);

        //判断准备要添加的数据是否已经存在
        if(obj!=null){
            //表示已经添加过该规格{"attributeName":"网络","attributeValue":["移动3G"]}

            //判断当前是选中规格还是取消规格
            if($event.target.checked){

                obj.attributeValue.push(value);

            }else{

                //获取当前取消规格选项的下标
                var index=obj.attributeValue.indexOf(value);

                obj.attributeValue.splice(index,1);

                //判断规格选项中还有没有选项
                if(obj.attributeValue.length==0){
                    //已经没有选项

                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(obj),1);

                }


            }





        }else{

            //表示第一次添加该规格
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});



        }




    }

    /**
     *
     * @param list 提交的规格实体
     * @param key attributeName
     * @param keyValue  本次选中 的规格名称
     *
     *  从已经拼装的数组中查看是否已经存在本次选中的规格
     *
     */
    searchObjectBykey=function(list,key,keyValue){

         for(var i=0;i<list.length;i++){


             if(list[i][key]==keyValue){


                 return list[i];
             }


         }

         return null;


    }









    //根据模板id查询模板对象
    $scope.$watch("entity.goods.typeTemplateId",function (newValue,oldValue) {

        //判断
        if(newValue){

            //初始化模板和扩展信息
            typeTemplateService.findOne(newValue).success(
                function (response) {
                    //记录模板对象信息
                    $scope.typeTemplate=response;
                    //获取品牌信息
                    $scope.typeTemplate.brandIds=JSON.parse(response.brandIds);
                    //获取扩展属性信息
                    //添加判断，当有id存在的时候，不用再次赋值

                    if(!$location.search()['id']){

                        $scope.entity.goodsDesc.customAttributeItems=JSON.parse(response.customAttributeItems);


                    }


                }
            );
            //初始化规格信息
            typeTemplateService.findSpecList(newValue).success(
                function (response) {

                    $scope.specList=response;

                }
            )





        }

    })

    
    //查询模板编号
    $scope.$watch("entity.goods.category3Id",function (newValue,oldValue) {

        //判断
        if(newValue){

            itemCatService.findOne(newValue).success(
                function (response) {

                    $scope.entity.goods.typeTemplateId=response.typeId;
                }
            )


        }



    })
    


    //初始化三级分类
    $scope.$watch("entity.goods.category2Id", function (newValue, oldValue) {


        //判断有没有最的值
        if (newValue) {

            itemCatService.findByParentId(newValue).success(

                function (response) {

                    $scope.itemCat3=response;
                }

            )


        }


    })



    //初始化二级分类
    /**
     *
     * $scope.$watch(参数一，function(参数二，参数三){})
     *
     *参数一：负责监听 的变量
     *参数二：变化后的值
     *参数三：变化前的值
     *
     */
    $scope.$watch("entity.goods.category1Id", function (newValue, oldValue) {


        //判断有没有最的值
        if (newValue) {

            itemCatService.findByParentId(newValue).success(

                function (response) {

                    $scope.itemCat2=response;
                }

            )


        }


    })

    //初始化一级分类
    $scope.selectItemCat1 = function (parentId) {

        itemCatService.findByParentId(parentId).success(
            function (response) {//List<TbItemCat>
                $scope.itemCat1 = response;

            }
        )


    }


    //定义实体对象
    /**
     * 问题：
     *      变量定义无法使用。
     * 原因：
     *   页面中是从上到下进行加载的，而在$scope.add的上面有定义
     *
     *   $scope.entity={goodsDesc:{}}
     *
     *   会造成上面变量定义的覆盖。
     */

    //        entity={goods:{},goodsDesc:{itemImages:[]}}
   // $scope.entity = {goods: {}, goodsDesc: {itemImages: []}};

    //添加图片对象
    $scope.addTableImageEntity = function () {

        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);

    }

    //删除图片对象
    $scope.deleTableImageEntity = function (index) {

        $scope.entity.goodsDesc.itemImages.splice(index, 1);

    }


    //图片上传
    $scope.uploadFile = function () {

        uploadService.uploadFile().success(
            function (response) {

                if (response.success) {

                    //变量记录图片上传后的路径
                    $scope.image_entity.url = response.message;


                } else {

                    alert(response.message);

                }

            }
        )


    }




    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function () {

        //id在地址上
        //http://localhost:9102/admin/goods_edit.html#?id=149187842868015
        var id=$location.search()['id'];
        // console.log($location.search());
        // console.log($location.search()['id']);


        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;

                //回显富文本内容
                editor.html(response.goodsDesc.introduction);

                //图片列表数据转换
                $scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);

                //扩展属性数据转换
                $scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);


                //规格列表数据转换
                $scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);


                //处理SKU的显示 spec为json字符串

                for(var i=0;i<$scope.entity.itemList.length;i++){

                    $scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);


                }



            }
        );
    }



    //$scope.entity={goodsDesc:{}}

    //添加方法
    $scope.add = function () {

        //添加商品商品介绍 editor
        $scope.entity.goodsDesc.introduction = editor.html();


        goodsService.add($scope.entity).success(
            function (response) {

                if (response.success) {

                    alert(response.message);
                    //清空列表
                    //$scope.entity = {};
                    $scope.entity={ goodsDesc:{itemImages:[],specificationItems:[]}  };
                    //清空富文本
                    editor.html("");

                } else {
                    alert(response.message);

                }


            }
        )


    }


    //保存
    $scope.save = function () {

        //添加商品商品介绍 editor
        $scope.entity.goodsDesc.introduction = editor.html();


        var serviceObject;//服务层对象

        if ($scope.entity.goods.id) {//如果有ID

            serviceObject = goodsService.update($scope.entity); //修改

        } else {

            serviceObject = goodsService.add($scope.entity);//增加

        }
        serviceObject.success(
            function (response) {
                if (response.success) {

                   /* $scope.entity={ goodsDesc:{itemImages:[],specificationItems:[]}  };
                    //清空富文本
                    editor.html("");

                    //重新查询
                    $scope.reloadList();//重新加载*/

                   //跳转到商品管理页面
                    location.href="/admin/goods.html";


                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

});	