layui.use(['form'], function () {
    var form = layui.form,
        $ = layui.$;

    //自定义验证规则
    form.verify({
        title: function (value) {
            if (value.length > 30) {
                return '标题长度在30个字符以内';
            }
        },
        content: function (value) {
            if (value.length > 100) {
                return '内容长度在100个字符以内';
            }
        },
    });

    //监听提交
    form.on('submit(saveBtn)', function (data) {
        axios({
            method: 'post',
            url: '/admin/notice',
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