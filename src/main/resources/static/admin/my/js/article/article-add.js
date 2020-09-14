layui.use(['layer', 'form', 'miniTab', 'rate'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        form = layui.form,
        miniTab = layui.miniTab

    //获取分类列表
    $.ajax({
        url: '/admin/category',
        type: 'GET',
        success: function (result) {
            if (result.code === 200) {
                $.each(result.data, function () {
                    var categoryId = $(this)[0].id;
                    var categoryName = $(this)[0].name;
                    var categoryOption = $('<option></option>').val(categoryId).text(categoryName);
                    $('#category-select').append(categoryOption);
                });
                form.render('select', 'form-filter');
            }
        }
    });
    //获取标签列表
    $.ajax({
        url: '/admin/tag',
        type: 'GET',
        success: function (result) {
            if (result.code == 200) {
                var tagList = [];
                $.each(result.data, function () {
                    var tag = {};
                    tag.value = $(this)[0].id;
                    tag.name = $(this)[0].name;
                    tagList.push(tag);
                });
                tagSelect.update({
                    data: tagList
                });
            }
        }
    });

    //渲染多选下拉框
    var tagSelect = xmSelect.render({
        el: '#tag-select',
        language: 'zn',
        //自动换行
        autoRow: true,
        //开启搜索
        filterable: true,
        max: 5,
        theme: {
            maxColor: 'orange',
        },
        create: function (val, arr) {
            return {
                name: val,
                value: val
            }
        },
        maxMethod() {
            layer.msg('标签数量不能超过5个');
        },
        data: []
    });
    //渲染编辑器
    var editor = editormd("test-editor", {
        placeholder: '本编辑器支持Markdown编辑，左边编写，右边预览',  //默认显示的文字，这里就不解释了
        width: "100%",
        height: "500px",
        saveHTMLToTextarea: true,
        path: "/static/admin/editormd/lib/"
    });

    //表单验证
    form.verify({
        verTitle: function (value) {
            if (value.length <= 0) {
                return '文章标题不能为空';
            }
            if (value.length > 100) {
                return '文章标题长度不能超过100';
            }
        },
        verSummary: function (value) {
            if (value.length > 100) {
                return '文章摘要长度不能超过100';
            }
        },
        verContent: function () {
            var content = editor.getMarkdown()
            if (content.length <= 0) {
                return '文章内容不能为空';
            }
        },
        verCategory: function (value) {
            if (value == 0) {
                return '请选择一个分类';
            }
        },
        verTag: function () {
            if (tagSelect.getValue('value').length <= 0) {
                return '请选择至少一个标签';
            }
        }
    });

    //获取输入
    function getField(data) {
        //获取标签列表
        data.field.tagList = tagSelect.getValue().map(item => {
            if (item.value === item.name) {
                item.value = null;
            }
            return {
                id: item.value,
                name: item.name,
            }
        });
        console.log(data.field.tagList);
        //获取文章内容
        data.field.content = editor.getHTML();
        //获取文章markdown
        data.field.textContent = editor.getMarkdown();
        return data.field;
    };
    //监听保存草稿按钮
    form.on('submit(draft)', function (data) {
        let fields = getField(data);
        //获取是否发布
        fields.published = false;
        axios({
            method: 'post',
            url: '/admin/article',
            data: fields,
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
                    miniTab.openNewTabByIframe({
                        href: "/admin/page/article/article-list?rad=" + Math.random(),
                        title: "文章管理",
                    });
                    //miniTab.deleteCurrentByIframe();
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
    //监听发布文章按钮
    form.on('submit(publish)', function (data) {
        let fields = getField(data);
        //获取是否发布
        fields.published = true;
        Swal.fire({
            title: '是否确定发布该文章？',
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: '确定发布！',
            cancelButtonText: '取消发布！',
            showLoaderOnConfirm: true,
            preConfirm: () => {
                return new Promise((resolve, reject) => {
                    axios({
                        method: 'post',
                        url: '/admin/article',
                        data: fields,
                        headers: {
                            'X-Requested-With': 'XMLHttpRequest'
                        }
                    }).then(response => {
                        resolve(response.data);
                    }, error => {
                        reject(error);
                    })
                }).then(res => {
                    Swal.fire({
                        icon: 'success',
                        title: '发布成功',
                        showConfirmButton: false,
                        timer: 1500,
                        onClose: () => {
                            miniTab.openNewTabByIframe({
                                href: "/admin/page/article/article-list?rad=" + Math.random(),
                                title: "文章管理",
                            });
                        }
                    });
                }).catch(error => {
                    Swal.showValidationMessage(`发布失败: ${error.response.data.message}`);
                })
            }
        });
        return false;
    });
});