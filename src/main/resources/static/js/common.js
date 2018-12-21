// 所有页面加载时，先执行获取用户信息的操作，填写头部的用户信息
$(function () {
    $.ajax({
        Accept: "application/json",
        type: "GET",
        url: "/getUserIdentity",
        success: function (response) {
            if (response) {
                // 获取用户
                var jsonStr = JSON.stringify(response);
                var jsonObj = JSON.parse(jsonStr);

                if (jsonObj.isLogin === "true") {
                    $("#login_name").text(jsonObj.username);
                    $("#login").hide();
                    $("#userDomain").show();
                } else {
                    $("#userDomain").hide();
                    $("#login").show();
                }
            }
        }
    });

    // 用户登录
    $("#userLogin").click(function () {
        var username = $("#userLoginName").val();
        var password = $("#password").val();
        $.ajax({
            Accept: "application/json",
            contentType: "application/json; charset=utf-8",
            type: "post",
            url: "/login",
            data: JSON.stringify({
                "username": username,
                "password": password
            }),
            success: function (response) {
                if (response) {
                    // 获取用户
                    var jsonStr = JSON.stringify(response);
                    var jsonObj = JSON.parse(jsonStr);
                    //
                    // if (jsonObj.isLogin === "true") {
                    $("#username").text(jsonObj.username);
                    $("#login").hide();
                    $("#userDomain").show();
                    if (document.referrer.indexOf("toLogin") > 0) {
                        window.location.href = "/";
                    }
                    window.location.href = document.referrer;
                    //     $("#login").hide();
                    //     $("#userDomain").show();
                    // } else {
                    //     $("#userDomain").hide();
                    //     $("#login").show();
                    // }
                }
            },
            // 非 200 - 400 的错误码都为 error
            error: function () {
                alert("用户名或密码错误，请确认");
            }
        });
    })
});
