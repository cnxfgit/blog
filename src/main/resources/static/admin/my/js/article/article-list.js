layui.use(['layer', 'form', 'table', 'miniTab', 'laydate'], function () {
    var $ = layui.jquery,
        form = layui.form,
        table = layui.table,
        miniTab = layui.miniTab,
        laydate = layui.laydate;

    //日期范围
    laydate.render({
        elem: '#date-range',
        range: '~',
        theme: 'molv'
    });

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
                form.render('select', 'search-filter');
            }
        }
    });

    table.render({
        elem: '#currentTableId',
        url: '/admin/article',
        toolbar: '#toolbarDemo',
        defaultToolbar: ['filter', 'exports', 'print', {
            title: '提示',
            layEvent: 'LAYTABLE_TIPS',
            icon: 'layui-icon-tips'
        }],
        cols: [
            [
                {type: "checkbox", width: 50, fixed: "left"},
                {field: 'id', width: 60, title: 'ID', sort: true},
                {
                    field: 'title', width: 250, title: '标题', sort: true, templet: function (data) {
                        let href = '/article/' + data.id;
                        return '<a href=' + href + ' style="color: #01AAED;" target="_blank">' + data.title + '</a>'
                    }
                },
                {
                    field: 'cover', width: 150, title: '封面', templet: function (data) {
                        let cover = data.cover;
                        return '<img src=' + cover + '></img>'
                    }
                },
                {
                    field: 'type', width: 80, title: '类型', sort: true, align: 'center', templet: function (data) {
                        let type = data.type === 1 ? '原创' : (data.type === 2 ? '转载' : '翻译');
                        let color = data.type === 1 ? 'green' : (data.type === 2 ? 'orange' : 'red');
                        let bgColor = 'layui-bg-' + color;
                        return '<span class="layui-badge ' + bgColor + '">' + type + '</span>';
                    }
                },
                {
                    field: 'category',
                    width: 150,
                    title: '分类',
                    align: 'center',
                    sort: true,
                    templet: function (data) {
                        let cateName = data.category.name;
                        let color = data.category.color;
                        return '<span class="layui-badge" style="background-color: ' + color +
                            ';color: #666666">' + cateName + '</span>';
                    }
                },
                {
                    field: 'tagList', width: 380, title: '标签', templet: function (data) {
                        let tagListStr = "";
                        $.each(data.tagList, function () {
                            let tagName = $(this)[0].name;
                            let color = $(this)[0].color;
                            let str = '<span class="layui-badge" style="background-color: ' + color +
                                ';color: #666666;margin-right: 3px">' + tagName + '</span>';
                            tagListStr += str;
                        })
                        return tagListStr;
                    }
                },
                {
                    field: 'published',
                    width: 120,
                    title: '发布状态',
                    align: 'center',
                    sort: true,
                    templet: function (data) {
                        let str = (data.published ? '发布' : '草稿');
                        let color = (data.published ? 'green' : 'orange');
                        let bgColor = 'layui-bg-' + color;
                        return '<span class="layui-badge ' + bgColor + '">' + str + '</span>';
                    }
                },
                {
                    field: 'status', width: 100, title: '审核状态', align: 'center', templet: function (data) {
                        let color, text, status = data.status;
                        if (status === 2) {
                            text = '审核通过';
                            color = 'green';
                        } else if (status === 1) {
                            text = '等待审核';
                            color = 'blue';
                        } else if (status === 0) {
                            text = '审核未过';
                            color = 'red';
                        }
                        let bgColor = 'layui-bg-' + color;
                        return '<span class="layui-badge ' + bgColor + '">' + text + '</span>';
                    }
                },
                {
                    field: 'views',
                    width: 100,
                    title: '浏览量',
                    align: 'center',
                    sort: true,
                    templet: function (data) {
                        let views = data.views;
                        let color = views >= 500 ? 'red' : (views >= 100 ? 'orange' : 'green');
                        let bgColor = 'layui-bg-' + color;
                        return '<span class="layui-badge ' + bgColor + '">' + views + '</span>';
                    }
                },
                {
                    field: 'likes',
                    width: 100,
                    title: '点赞数',
                    align: 'center',
                    sort: true,
                    templet: function (data) {
                        let likes = data.likes;
                        let color = likes >= 100 ? 'red' : (likes >= 50 ? 'orange' : 'green');
                        let bgColor = 'layui-bg-' + color;
                        return '<span class="layui-badge ' + bgColor + '">' + likes + '</span>';
                    }
                },
                {
                    field: 'comments',
                    width: 100,
                    title: '评论数',
                    align: 'center',
                    sort: true,
                    templet: function (data) {
                        let comments = data.comments;
                        let color = comments >= 30 ? 'red' : (comments >= 10 ? 'orange' : 'green');
                        let bgColor = 'layui-bg-' + color;
                        return '<span class="layui-badge ' + bgColor + '">' + comments + '</span>';
                    }
                },
                {
                    width: 150, title: '作者', sort: true, templet: function (data) {
                        return data.author.username;
                    }
                },
                {field: 'createTime', width: 200, title: '创建时间', sort: true},
                {field: 'updateTime', width: 200, title: '更新时间', sort: true},
                {
                    title: '操作',
                    minWidth: 180,
                    toolbar: '#currentTableBar',
                    fixed: "right",
                    align: "center",
                    hide: (!hasPermission('blog:article:audit') && !hasPermission('blog:article:edit') && !hasPermission('blog:article:delete'))
                }
            ]
        ],
        limits: [10, 15, 20, 25, 50, 100],
        limit: 15,
        page: true,
        done: function () {
            checkPermission();
        }
    });

    // 监听搜索操作
    form.on('submit(data-search-btn)', function (data) {
        let title = data.field.title;
        let type = data.field.type;
        let categoryId = data.field.categoryId;
        let published = data.field.published;
        let status = data.field.status;
        let dateRange = data.field.dateRange;
        let dates = dateRange.split(' ~ ')
        let startDate = dates[0];
        let endDate = dates[1];
        //执行搜索重载
        table.reload('currentTableId', {
            page: {
                curr: 1
            }
            , where: {
                title: title,
                type: type,
                categoryId: categoryId,
                published: published,
                status: status,
                startDate: startDate,
                endDate: endDate,
            }
        }, 'data');

        return false;
    });

    /**
     * toolbar监听事件
     */
    table.on('toolbar(currentTableFilter)', function (obj) {
        if (obj.event === 'delete') {  // 监听删除操作
            var checkStatus = table.checkStatus('currentTableId')
                , data = checkStatus.data;
            var idList = [];
            $.each(data, function () {
                idList.push($(this)[0].id);
            });
            if (idList.length <= 0) {
                return false;
            }
            Swal.fire({
                title: '确定删除选中的' + idList.length + '篇文章吗？',
                text: '你将无法恢复它！',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: '确定删除！',
                cancelButtonText: '取消删除！',
                showLoaderOnConfirm: true,
                preConfirm: () => {
                    return new Promise((resolve, reject) => {
                        axios({
                            method: 'delete',
                            url: '/admin/article',
                            data: idList,
                            headers: {
                                'X-Requested-With': 'XMLHttpRequest'
                            }
                        })
                            .then(response => {
                                table.reload('currentTableId');
                                resolve(response.data);
                            }, error => {
                                reject(error);
                            })
                    }).then(res => {
                        Swal.fire({
                            icon: 'success',
                            title: '删除成功！',
                            showConfirmButton: false,
                            timer: 1500
                        });
                    }).catch(error => {
                        Swal.showValidationMessage(`删除失败: ${error.response.data.message}`);
                    })
                }
            });
        }
    });

    table.on('tool(currentTableFilter)', function (obj) {
        var data = obj.data;
        if (obj.event === 'edit') {
            miniTab.openNewTabByIframe({
                href: "/admin/article/" + data.id,
                title: "文章编辑",
            });
            return false;
        } else if (obj.event === 'delete') {
            Swal.fire({
                title: '确定删除选中的这篇文章吗？',
                text: '你将无法恢复它！',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: '确定删除！',
                cancelButtonText: '取消删除！',
                showLoaderOnConfirm: true,
                preConfirm: () => {
                    return new Promise((resolve, reject) => {
                        axios({
                            method: 'delete',
                            url: '/admin/article/' + data.id,
                            headers: {
                                'X-Requested-With': 'XMLHttpRequest'
                            }
                        })
                            .then(response => {
                                obj.del();
                                resolve(response.data);
                            }, error => {
                                reject(error);
                            })
                    }).then(res => {
                        Swal.fire({
                            icon: 'success',
                            title: '删除成功！',
                            showConfirmButton: false,
                            timer: 1500
                        });
                    }).catch(error => {
                        Swal.showValidationMessage(`删除失败: ${error.response.data.message}`);
                    })
                }
            });
        } else if (obj.event === 'audit') {
            async function audit() {
                const inputOptions = new Promise((resolve) => {
                    resolve({
                        '0': '审核未过',
                        '2': '审核通过',
                    });
                })
                const {value: status} = await Swal.fire({
                    title: '审核结果',
                    input: 'radio',
                    inputOptions: inputOptions,
                    inputValidator: (value) => {
                        if (!value) {
                            return '请选择审核结果！'
                        }
                    }
                })
                if (status) {
                    axios({
                        method: 'put',
                        url: '/admin/article/audit',
                        data: {id: data.id, status: status}
                    }).then((response) => {
                        table.reload('currentTableId');
                        Swal.fire({
                            icon: 'success',
                            title: '审核成功',
                            showConfirmButton: false,
                            timer: 1500
                        });
                    }).catch((error) => {
                        Swal.fire({
                            icon: 'error',
                            title: '保存失败！',
                            text: `${error.response.data.message}`,
                        });
                    });
                }
            }

            audit();
        }
    });

});