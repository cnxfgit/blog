layui.use(['form'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.$;

    //渲染多选下拉框
    var colorSelect = xmSelect.render({
        el: '#color-select',
        language: 'zn',
        //开启搜索
        filterable: true,
        radio: true,
        clickClose: true,
        model: {
            label: {
                type: 'text',
                text: {
                    template: function (item, sels) {
                        return '<span>' + item.name + '</span>';
                    },
                },
            }
        },
        template({name, value}) {
            return name + '<span style="position: absolute; right: 10px; background-color: ' + value + '">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>'
        },
        data: [
            {name: '#D5F5E3', value: '#D5F5E3'},
            {name: '#E8F8F5', value: '#E8F8F5'},
            {name: '#82E0AA', value: '#82E0AA'},
        ],
        create: function (val, arr) {
            if (arr.length === 0) {
                return {
                    name: '创建' + val,
                    value: val,
                }
            }
        },
    });
    //获取所有颜色
    $.ajax({
        url: '/admin/category/colors',
        method: 'GET',
        success: function (result) {
            let data = result.data.map(item => {
                return {
                    name: item,
                    value: item
                }
            });
            colorSelect.update({
                data: data,
                autoRow: true,
            })
        }
    });
    //表单验证
    form.verify({
        colorVer: function () {
            if (colorSelect.getValue('value').length <= 0) {
                return '请选择颜色';
            }
            let value = colorSelect.getValue('value')[0];
            if (value[0] !== '#') {
                return '颜色由#开头';
            }
            let pattern = /^#([0-9a-fA-F]{6})$/;
            if (!pattern.test(value)) {
                return '颜色由#开头的6位16进制字符组成';
            }
        }
    });
    //监听提交
    form.on('submit(saveBtn)', function (data) {
        data.field.color = colorSelect.getValue('value')[0];
        axios({
            method: 'post',
            url: '/admin/category',
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