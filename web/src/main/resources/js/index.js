var setting = {
    view: {
        dblClickExpand: false,
        showLine: true,
        selectedMulti: false
    },
    data: {
        simpleData: {
            enable: true,
            idKey: "id",
            pIdKey: "parentId",
            rootPId: "0"
        }
    },
    callback: {
        onClick: function (event, treeId, treeNode) {
            //this.beforeClick(treeId, treeNode);
            console.log("========>click" + treeId + "," + JSON.stringify(treeNode));
            return false;
        },
        beforeClick: function (treeId, treeNode) {
            if (treeNode.urlpath != null && treeNode.urlpath != "") {
                var tabTile = treeNode.name;
                addTab(tabTile, treeNode.urlpath);
            }
            return false;
        }
    }
};


$(function () {


    $.ajax({
        type: "GET",
        url: "/authmenu/findtree",
        dataType: 'json',
        success: function (msg) {
            $.fn.zTree.init($("#menutree"), setting, msg.data);

            $.ajax({
                type: "GET",
                url: "/authmenu/curuser",
                dataType: 'json',
                success: function (msg) {
                    if (msg.code == 200) {
                        $("#loginUser").text(msg.data.username);
                    } else {
                        console.log("===>校验当前用户信息:" + JSON.stringify(msg));
                        //未登录成功
                         window.location.href = "/html/login.html";
                    }
                }, error: function (a,b) {
                    window.location.href = "/html/login.html";
                }
            });
        }, error: function (msg) {
            window.location.href = "/html/login.html";
        }
    });


    //
    // $("#themeList li").unbind().bind("click", function () {
    //     $("#themeList li>div").removeClass("selected");
    //     $(this).find("div").addClass("selected");
    //     $("#themes").attr("href", "/css/admin/themes/" + $(this).attr("theme") + "/easyui.css");
    // });


    // $("#menutree  ul li a").unbind().bind("click", function () {
    //     var tabTile = $(this).html();
    //     var url = $(this).attr("rel");
    //     addTab(tabTile, url);
    // });
    //setFLgx();

    $("#moditypwdBut").unbind().bind("click", function () {
        $("#oldpwd").val("");
        $("#newpwd").val("");
        $("#cmpwd").val("");
        $("#modifyPWDDialog").dialog("open");
    });

    $("#saveModifyPWDBut").unbind().bind("click", function () {
        ajaxModifyForm("modifypwd.shtml");
    });
    bodyLoad();


});

function bodyLoad() {
    var dateTime = new Date();
    var hh = dateTime.getHours();
    var mm = dateTime.getMinutes();
    var ss = dateTime.getSeconds();

    hh = hh > 9 ? ("" + hh) : ("0" + hh);
    mm = mm > 9 ? ("" + mm) : ("0" + mm);
    ss = ss > 9 ? ("" + ss) : ("0" + ss)


    var yy = dateTime.getFullYear();
    var MM = dateTime.getMonth() + 1;  //因为1月这个方法返回为0，所以加1
    var dd = dateTime.getDate();

    MM = MM > 9 ? ("" + MM) : ("0" + MM);
    dd = dd > 9 ? ("" + dd) : ("0" + dd);

    var week = dateTime.getDay();

    var days = ["日 ", "一 ", "二 ", "三 ", "四 ", "五 ", "六 "]
    $("#dateVal").html(yy + "年" + MM + "月" + dd + "日 " + "星期" + days[week] + " " + hh + ":" + mm + ":" + ss);
    setTimeout(bodyLoad, 1000);
}


function CharMode(iN) {
    if (iN >= 48 && iN <= 57) //数字
        return 1;
    if (iN >= 65 && iN <= 90) //大写字母
        return 2;
    if (iN >= 97 && iN <= 122) //小写
        return 4;
    else
        return 8; //特殊字符
}

//bitTotal函数
//计算出当前密码当中一共有多少种模式
function bitTotal(num) {
    modes = 0;
    for (i = 0; i < 4; i++) {
        if (num & 1) {
            modes++;
        }
        num >>>= 1;
    }
    return modes;
}

//checkStrong函数
//返回密码的强度级别

function checkStrong(sPW) {
    if (sPW.length <= 4) {
        return 0; //密码太短
    }
    Modes = 0;
    for (i = 0; i < sPW.length; i++) {
        //测试每一个字符的类别并统计一共有多少种模式.
        Modes |= CharMode(sPW.charCodeAt(i));
    }

    return bitTotal(Modes);

}

//pwStrength函数
//当用户放开键盘或密码输入框失去焦点时,根据不同的级别显示不同的级别

function pwStrength(pwd) {
    if (pwd == null || pwd == '') {
        var result = '安全系数危险！';
    }
    else {
        S_level = checkStrong(pwd);
        switch (S_level) {
            case 0:
                var result = '安全系数危险！';
            case 1:
                var result = '安全系数低！';
                break;
            case 2:
                var result = '安全系数中！';
                break;
            default:
                var result = '安全系数高！';
        }
    }
    $("#result").html(result);
    return;
}


function ajaxModifyForm(url) {

    $('#modifyPWDForm').form({
        url: url,
        onSubmit: function () {
            var oldpwd = $("#oldpwd").val();
            var newpwd = $("#newpwd").val();
            var cmpwd = $("#cmpwd").val();
            if (oldpwd == "") {
                $.messager.alert("提示信息", "旧密码为空！", "error");
                return false;
            } else if (newpwd == "") {
                $.messager.alert("提示信息", "新密码为空！", "error");
                return false;
            } else if (cmpwd == "") {
                $.messager.alert("提示信息", "确认密码为空！", "error");
                return false;
            } else if (cmpwd != newpwd) {
                $.messager.alert("提示信息", "确认密码错误！", "error");
                return false;
            } else if (newpwd.length <= 6) {
                $.messager.alert("提示信息", "密码长度大于6位！", "error");
                return false;
            } else if (newpwd.length > 30) {
                $.messager.alert("提示信息", "密码长度小于30位！", "error");
                return false;
            } else if ($("#result").text() == "安全系数低！") {
                $.messager.alert("提示信息", "安全系数低！", "error");
                return false;
            }
        },
        success: function (data) {
            if (data == 1) {
                $.messager.alert("提示信息", "修改成功！！", "info");
                $('#modifyPWDDialog').dialog('close');
            } else if (data == -1) {
                $.messager.alert("提示信息", "旧密码错误！", "error");
            } else {
                $.messager.alert("提示信息", "修改密码失败！", "error");
            }
        }

    });
    $('#modifyPWDForm').submit();
}


function setFLgx() {
    var saveurl = "security/menu/save.shtml";
    var appId = "45";
    var pid = "0";
    var menuLevel = 1;
    var name = "";
    /**
     *    <option value="1">可分配</option>
     <option value="2">不可分配</option>
     * @type {*}
     */
    var menuType = "1";
    var href = "";
    var url = "";
    var orderNo = 1;
    /**
     *    <option value="1">是</option>
     <option value="0">否</option>
     * @type {string}
     */
    var enable = "1";
    /**
     *    <option value="0">否</option>
     <option value="1">是</option>
     * @type {string}
     */
    var baseUrlFlag = "0";
    var field = "";
    $("#nav >div").each(function (index, element) {
        // alert($(this).prop("title"));
        name = $(this).prop("title");
        if ($(this).attr("appId") != undefined && $(this).attr("appId") != "") {
            appId = $(this).attr("appId");

        } else {
            appId = "89";
        }
        saveMenu(saveurl, appId, pid, menuLevel, name, menuType, href, url, index + 1, enable, baseUrlFlag, field, $(this).children("ul"));

    });

}

/**
 *
 *
 * @param appId  45
 * @param pid
 * @param menuLevel
 * @param name
 * @param menuType
 * @param href
 * @param url
 * @param orderNo
 * @param enable
 * @param baseUrlFlag
 */
function saveMenu(saveurl, appId, pid, menuLevel, name, menuType, href, url, orderNo, enable, baseUrlFlag, field, obj) {
    $.post(saveurl, {
        appId: appId,
        pid: pid,
        menuLevel: menuLevel,
        name: name,
        menuType: menuType,
        href: href,
        url: url,
        orderNo: orderNo,
        enable: enable,
        baseUrlFlag: baseUrlFlag,
        field: field
    }, function (data) {
        var menuLevels = menuLevel;
        if (obj == null) return;
        $(obj).children("li").each(function (index, item) {
            if ($(this).children("ul").length > 0) {
                saveMenu(saveurl, appId, data.id, menuLevels + 1, $.trim($(this).children("span").text()), menuType, "", "", index + 1, enable, baseUrlFlag, field, $(this).children("ul"));
            } else {
                var aRel = $(this).find("a").prop("rel");
                saveMenu(saveurl, appId, data.id, menuLevels + 1, $.trim($(this).text()), menuType, aRel, subUrl(aRel), index + 1, enable, baseUrlFlag, field, null);
            }
        });
    }, "json")
}

function subUrl(str) {
    return "/" + str.substring(0, str.lastIndexOf("/") + 1) + "**"
}

/**
 * 添加Tab
 * @param subtitle
 * @param url
 * @param icon
 */
function addTab(subtitle, url) {
    if (url.indexOf("?") > -1) {
        url = url + "&_date=" + new Date().getTime();
    } else
        url = url + "?_date=" + new Date().getTime();
    if (!$('#tabs').tabs('exists', subtitle)) {
        $('#tabs').tabs('add', {
            title: subtitle,
            content: createFrame(url),
            closable: true,
            cache: false
        });
    } else {
        $('#tabs').tabs('select', subtitle);
        $('#mm-tabupdate').click();
        /**
         * 重新刷新
         * @type {*|jQuery}
         */
        var tab = $("#tabs").tabs('getSelected');
        $("#tabs").tabs('update', {
                tab: tab,
                options: {
                    content: createFrame(url)
                }
            }
        );

    }
    tabClose();
}

/**
 * 添加一个Tab URL
 * @param url
 * @returns {string}
 */
function createFrame(url) {
    var s = '<iframe id="iframeCenter"scrolling="auto" frameborder="0"  src="' + url + '" style="width:100%;height:100%;"></iframe>';
    return s;
}

/**
 * 关闭Tab
 */
function tabClose() {

    $(".tabs-inner").dblclick(function () {
        var subtitle = $(this).children(".tabs-closable").text();
        $('#tabs').tabs('close', subtitle);
    })

    $(".tabs-inner").bind('contextmenu', function (e) {
        $('#mm').menu('show', {
            left: e.pageX,
            top: e.pageY
        });

        var subtitle = $(this).children(".tabs-closable").text();

        $('#mm').data("currtab", subtitle);
        $('#tabs').tabs('select', subtitle);
        return false;
    });
}



