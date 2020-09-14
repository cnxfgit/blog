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
        url: '/admin/notice',
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
                {field: 'title', width: 150, title: '标题'},
                {field: 'content', width: 250, title: '内容'},
                {
                    field: 'display',
                    width: 150,
                    title: '是否前台显示',
                    sort: true,
                    align: 'center',
                    templet: function (data) {
                        let text = data.display ? '是' : '否';
                        let color = data.display ? 'green' : 'orange';
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
                    hide: (!hasPermission('sys:notice:edit') && !hasPermission('sys:notice:delete'))
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
        let dateRange = data.field.dateRange;
        let dates = dateRange.split(' ~ ')
        let startDate = dates[0];
        let endDate = dates[1];
        let display = data.field.display;

        //执行搜索重载
        table.reload('currentTableId', {
            page: {
                curr: 1
            }
            , where: {
                title: title,
                startDate: startDate,
                endDate: endDate,
                display: display,
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
                title: '确定删除选中的' + idList.length + '条公告吗？',
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
                            url: '/admin/notice',
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
                title: '添加公告',
                type: 2,
                shade: 0.2,
                maxmin: true,
                shadeClose: true,
                area: ['50%', '60%'],
                content: '/admin/page/notice/notice-add',
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
                title: '确定删除选中的这条公告吗？',
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
                            url: '/admin/notice/' + data.id,
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
                title: '编辑公告',
                type: 2,
                shade: 0.2,
                maxmin: true,
                shadeClose: true,
                area: ['50%', '80%'],
                content: '/admin/notice/' + data.id,
            });
            $(window).on("resize", function () {
                layer.full(index);
            });
        }
    });
});