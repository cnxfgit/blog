layui.use(['form'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.$;

    //渲染数据
    $('.layui-form input[name="id"]').val(link.id);
    $('.layui-form input[name="nickname"]').val(link.nickname);
    $('.layui-form input[name="avatar"]').val(link.avatar);
    $('.layui-form textarea[name="introduction"]').val(link.introduction);
    $('.layui-form input[name="link"]').val(link.link);
    $('.layui-form input[name="sort"]').val(link.sort);
    form.render();

    //自定义验证规则
    form.verify({
        //验证昵称
        nickname: function (value) { //value：表单的值、item：表单的DOM对象
            if (value.length < 1 || value.length > 10) {
                return '昵称长度为1-10个字符'
            }
        },
        introduction: function (value) {
            if (value.length > 30) {
                return '简介长度在30个字符以内';
            }
        }
    });

    //监听提交
    form.on('submit(saveBtn)', function (data) {
        axios({
            method: 'put',
            url: '/admin/link',
            data: data.field,
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            },
        }).then((response) => {
            Swal.fire({
                icon: 'success',
                title: '保存成功',
                showConfirmButton: false,
                timer: 1500,
                onClose: () => {
                    var parentLayer = parent.layer;
                    var iframeIndex = parentLayer.getFrameIndex(window.name);
                    parent.location.reload();
                    parentLayer.close(iframeIndex);
                }
            });
        }).catch((error) => {
            Swal.fire({
                icon: 'error',
                title: '保存失败！',
                text: `${error.response.data.message}`,
            });
        })
        return false;
    });

});