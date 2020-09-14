layui.use(['form'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.$;
    //字数校验
    window.wordLeg = function (obj) {
        var currleg = $(obj).val().length;
        var length = $(obj).attr('maxlength');
        if (currleg > length) {
            Swal.fire({
                toast: true,
                position: 'center',
                icon: 'warning',
                title: '字数请在' + length + '字以内',
                showConfirmButton: false,
                timer: 3000,
                timerProgressBar: true,
            });
        } else {
            $('.text_count').text(currleg);
        }
    }
    //自定义验证规则
    form.verify({
        reply: function (value) {
            if (value === '' || value.length <= 0) {
                return '内容不能为空';
            }
            if (value.length > 80) {
                return '内容长度不能超过80个字符';
            }
        }
    });
    //监听提交
    form.on('submit(saveBtn)', function (data) {
        axios({
            method: 'post',
            url: '/admin/comment',
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