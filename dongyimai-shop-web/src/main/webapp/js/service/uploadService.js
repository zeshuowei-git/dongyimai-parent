
//文件（图片）上传服务
app.service("uploadService",function ($http) {


    this.uploadFile=function(){

        //创建一个对象
        /**
         * formDate:
         *       存储图片的数据
         *       key=value
         *  作用：
         *     key=“file”
         *     value=图片上传的实际内容
         *
         *
         *file:
         *   是页面中所有的上传组件
         *
         * file.files[0]:选择file中第一个上传组件
         *
         *
         * headers: {'Content-Type':undefined},
         *
         * Content-Type：设置上传的方式
         * 文件上传的方式：
         *    multipult/form-data
         *
         *    注意：当我们将Content-Type设置为undefined时，浏览器自动会赋值为multipult/form-data
         *
         *
         * transformRequest: angular.identity
         *   作用进行上传数据的序列化
         *
         * @type {formDate}
         */
        var formData=new FormData();
        formData.append("file",file.files[0]);

        return $http({
            method:'POST',
            url:"../upload.do",
            data: formData,
            headers: {'Content-Type':undefined},
            transformRequest: angular.identity
        });



    }


})