app.controller("itemController",function($scope,$http){

//添加商品到购物车
    $scope.addToCart=function(){
        $http.get("http://localhost:9107/cart/addGoodsToCartList.do?itemId="+$scope.sku.id+"&num="+$scope.num,{'withCredentials':true}).success(
            function (response) {
                if (response.success){
                    location.href='http://localhost:9107/cart.html';//跳转到购物车页面
                }else{
                    alert(response.message);
                }
            }
        )
    }


    //加入购物车
    $scope.addCart=function(){


        alert("skuId"+$scope.sku.id);

    }

    //选中赋值sku
    searchSku=function(){


        //遍历skuList
        for(var i=0;i<skuList.length;i++){

            var skus=JSON.parse(skuList[i].spec);
            //判断
            if(metchObject(skus,$scope.specifacationItems)){

                //规格相等
                $scope.sku=skuList[i];

            }



        }


    }


    //定义比较两个对象是否相同

    metchObject=function(map1,map2){


        //遍历map1 k==kye
        for (var k in map1){

            if(map1[k]!=map2[k]){
                return false;

            }

        }

        //遍历map2
        for(var k in map2){

            if(map2[k]!=map1[k]){

                return false;

            }


        }


        return true;


    }

    //加载默认的sku
    $scope.loadSku=function(){
        //获取sku列表中的一个sku
        $scope.sku=skuList[0];
        //设置默认选中的规格
        $scope.specifacationItems=JSON.parse(JSON.stringify($scope.sku.spec));
        //转换
        $scope.specifacationItems=JSON.parse($scope.specifacationItems);

    }



    //记录用户选择规格和规格选项
    $scope.specifacationItems={};

    //定义是否选中
    $scope.isSelected=function(name,value){

        //判断是否存在
        if($scope.specifacationItems[name]==value){


            return true;
        }else{

            return false;
        }



    }



    //记录选中的规格和规格选项
    $scope.selectSpecification=function(name,value){
        /**
         * name:规格名称
         * value:规格选项名称
         *
         *  数据格式：
         *   {"网络"："移动3G"..}
         */
        $scope.specifacationItems[name]=value;
        //调用比对方法
        searchSku();

    }



    //商品数量操作
    $scope.addNum=function(x){

        $scope.num=$scope.num+x;
        //判断
        if($scope.num<1){

            $scope.num=1;
        }

    }

	
	
	
	
})