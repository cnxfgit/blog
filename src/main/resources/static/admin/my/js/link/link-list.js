layui.use(['form', 'table', 'laydate'], function () {
    var $ = layui.jquery,
        form = layui.form,
        table = layui.table,
        laydate = layui.laydate;
    //日期范围
    laydate.render({
        elem: '#date-range',
        range: '~',
        theme: 'molv'
    });

    table.render({
        elem: '#currentTableId',
        url: '/admin/link',
        toolbar: '#toolbarDemo',
        defaultToolbar: ['filter', 'exports', 'print', {
            title: '提示',
            layEvent: 'LAYTABLE_TIPS',
            icon: 'layui-icon-tips'
        }],
        cols: [
            [
                {type: "checkbox", width: 50, fixed: "left"},
                {field: 'id', width: 80, title: 'ID', sort: true},
                {field: 'nickname', width: 150, title: '昵称'},
                {field: 'introduction', width: 250, title: '简介'},
                {
                    field: 'link', title: '网址', minWidth: 200, templet: function (data) {
                        let link = data.link;
                        return '<a href=' + link + ' style="color: #01AAED;" target="_blank">' + link + '</a>'
                    }
                },
                {
                    field: 'sort', width: 80, align: 'center', title: '排序', sort: true, templet: function (data) {
                        let sort = data.sort;
                        return '<span class="layui-badge layui-bg-green">' + sort + '</span>'
                    }
                },
                {
                    field: 'status',
                    width: 100,
                    title: '状态',
                    align: 'center',
                    sort: true,
                    templet: function (data) {
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
                {field: 'createTime', title: '创建时间', minWidth: 200, sort: true},
                {field: 'updateTime', title: '更新时间', minWidth: 200, sort: true},
                {
                    title: '操作',
                    minWidth: 150,
                    width: 200,
                    toolbar: '#currentTableBar',
                    fixed: "right",
                    align: "center",
                    hide: (!hasPermission('blog:link:audit') && !hasPermission('blog:link:edit') && !hasPermission('blog:link:delete'))
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
        let nickname = data.field.nickname;
        let dateRange = data.field.dateRange;
        let dates = dateRange.split(' ~ ')
        let startDate = dates[0];
        let endDate = dates[1];
        let status = data.field.status;

        //执行搜索重载
        table.reload('currentTableId', {
            page: {
                curr: 1
            }
            , where: {
                nickname: nickname,
                startDate: startDate,
                endDate: endDate,
                status: status,
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
                title: '确定删除选中的' + idList.length + '个友链吗？',
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
                            url: '/admin/link',
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
        } else if (obj.event === 'add') {
            var index = layer.open({
                title: '添加友链',
                type: 2,
                shade: 0.2,
                maxmin: true,
                shadeClose: true,
                area: ['50%', '60%'],
                content: '/admin/page/link/link-add',
            });
            $(window).on("resize", function () {
                layer.full(index);
            });
        }
    });

    table.on('tool(currentTableFilter)', function (obj) {
        var data = obj.data;
        if (obj.event === 'delete') {
            Swal.fire({
                title: '确定删除选中的这条友链吗？',
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
                            url: '/admin/link/' + data.id,
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
        } else if (obj.event === 'edit') {
            var index = layer.open({
                title: '编辑友链',
                type: 2,
                shade: 0.2,
                maxmin: true,
                shadeClose: true,
                area: ['50%', '80%'],
                content: '/admin/link/' + data.id,
            });
            $(window).on("resize", function () {
                layer.full(index);
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
                        url: '/admin/link/audit',
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