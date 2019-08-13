function fixWidth(percent) {
    return document.body.clientWidth * percent;
};

function ecp888(params) {
    var ecp888 = this.pp = this;
    var dp = this.dp = params;
    var action = this.action = dp.form.attr("action");
    this.init = function () {
        dp.table.datagrid({
            url: "/authmenu/findAllList",
            method: 'GET',
            queryParams: ecp888.searchParams(),
            width: '100%',
             pageNumber: 1,
             pageList: [50, 100, 200],
            height: $(document).height(),
            rownumbers: true,
            singleSelect: true,
            fitColumns: true,
            sortOrder: 'asc',
             pagination: true,
             pageSize: 50,
            toolbar: dp.toolbar,
            columns: dp.columns,
            loadFilter: function (data) {
                var result = {};
                result.rows = data;
                result.total = 0;
                result.pageNo = 50;
                return result;
            }
        });
    }
    this.searchParams = function () {
        var fields = dp.form.serializeArray();
        var formDatas = {};
        $.each(fields, function (i, o) {
            if (!$.isEmptyObject(o.value)) {
                formDatas[o.name] = o.value;
            }
        });
        console.log("==>查询条件"+JSON.stringify(formDatas));
        return formDatas;
    }
    this.search = function () {
        dp.table.datagrid('load', ecp888.searchParams());
    }
    this.toAdd = function () {
        dp.dialog.dialog({title: "新增"});
        var fields = dp.dialog.find("form .field");
        var field;
        $.each(fields, function (i, o) {
            field = $(o);

            var name = field.attr("name");
            if (name == "pwd") {
                document.getElementById("pwd").style.display = "";
            }

            if (field.is("input") || field.is("select")) {
                if ("radio" == field.attr("type")) {
                    field.removeProp("checked");
                    dp.dialog.find("[name=" + field.attr("name") + "]").eq(0).prop("checked", "checked");
                } else {
                    field.val('');
                }
            } else if (field.is("textarea")) {
                field.text('');
            }
        });
        dp.dialog.dialog("open");
    }
    this.toEdit = function (index) {
        dp.dialog.dialog({title: "修改"});
        dp.dialog.find("form")[0].reset();
        var rows = dp.table.datagrid('getRows');
        var fields = dp.dialog.find("form .field");
        var name;
        var field;
        var datas = rows[index];

        $.each(fields, function (i, o) {
            field = $(o);
            name = field.attr("name");
            if (name == "pwd") {
                document.getElementById("pwd").style.display = "none";
            }
            if (field.is("input") || field.is("select")) {
                if ("radio" == field.attr("type")) {
                    field.removeProp("checked");
                    if ($.trim(datas[name]) == field.val()) {
                        field.prop("checked", "checked");
                    }
                } else {
                    field.val(datas[name]);
                }
            } else if (field.is("textarea")) {
                field.text(null != datas[name] ? datas[name] : '');
            }
        });
        dp.dialog.dialog("open");
    }
    this.updatePwd = function (index) {
        var rows = dp.table.datagrid('getRows');
        var fields = $("#updatePwd").find("form .field");
        var name;
        var field;
        var datas = rows[index];
        $.each(fields, function (i, o) {
            field = $(o);
            name = field.attr("name");
            if (field.is("input")) {
                if (name == "pwd" || name == "repwd") {
                    field.val('');
                } else if (name == "id") {
                    field.val(datas[name]);
                }
            }
        });
        $("#updatePwd").dialog("open");
    }
    this.toDelete = function (id) {
        $.messager.confirm("删除确认", "您确认要删除数据？", function (data) {
            if (data) {
                ecp888.ajaxHelp({
                    url: action + "/deleteById.shtml",
                    data: {id: id},
                    success: function (json) {
                        if (200 == json.code) {
                            $.messager.alert("删除提示", json.message);
                            dp.table.datagrid("reload");
                        } else {
                            $.messager.alert("删除提示", json.message);
                        }
                    }
                });
            }
        });
    }
    /**
     * 级联删除菜单
     */
    this.toCascadeDeleteMenu = function (id) {
        $.messager.confirm("删除确认", "您确认要删除数据？", function (data) {
            if (data) {
                ecp888.ajaxHelp({
                    url: "/authmenu/deleteCashcade",
                    data: {id: id,'_method':'delete'},
                    success: function (json) {
                        if (200 == json.code) {
                            $.messager.alert("删除提示", json.message);
                            dp.table.datagrid("reload");
                        } else {
                            $.messager.alert("删除提示", json.message);
                        }
                    }
                });
                // $.ajax({
                //     url : "user/" + $(this).attr("id"),
                //     type : "post",
                //     data : $("#userUpdateModal form").serialize()+ "&_method=put",
                //     success : function(result) {
                //         console.log(result.msg);
                //     }
                // });
            }
        });
    }
    this.save = function () {
        if (ecp888.validateForm(dp.dialog.find("form"))) {
            ecp888.ajaxHelp({
                url: "/authmenu/add",
                data: dp.dialog.find("form").serialize(),
                success: function (json) {
                    if (200 == json.code) {
                        ecp888.closeDialog();
                        $.messager.alert("保存提示", json.message);
                        dp.table.datagrid("reload");
                    } else {
                        $.messager.alert("保存提示", json.message);
                    }
                }
            });
        }
    }
    this.saveNewPwd = function () {
        if (ecp888.validateForm($('#updatePwd').find("form"))) {
            if ($('#upwd').val() == $('#reupwd').val()) {
                ecp888.ajaxHelp({
                    url: action + "/saveNewPwd.shtml",
                    data: $('#updatePwd').find("form").serialize(),
                    success: function (json) {
                        if (0 == json.code) {
                            ecp888.closeDialog('#updatePwd');
                            $.messager.alert("保存提示", json.msg);
                            dp.table.datagrid("reload");
                        } else {
                            $.messager.alert("保存提示", json.msg);
                        }
                    }
                });
            } else {
                $.messager.alert("错误提示", "两次输入密码不匹配");
            }
        }
    }
    this.closeDialog = function (selector) {
        if (selector) {
            $(selector).dialog("close");
        } else {
            dp.dialog.dialog("close");
        }
    }
    this.ajaxHelp = function (params) {
        $.ajax($.extend({
            type: "POST",
            cache: false,
            dataType: "JSON",
        }, params));
    }
    this.validateForm = function (form) {
        return form.form('validate');
    }
}