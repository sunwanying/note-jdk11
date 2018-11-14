/**
 * @author GuoLei Sun
 * Date: 2018/11/9 11:22 AM
 */
$(function () {

    // 笔记 markdown 预览
    $("#previewButton").click(function () {
        var content = $("#content").val();
        var markdown_content = gfmMarkdown(content);

        if (markdown_content === "error") {
            markdown_content = markdown.toHTML(content);
        }

        $("#preview").html(markdown_content);
    });

    // 自动换行缩进
    var textarea = document.querySelector("textarea");
    indent(textarea);
});

// 缩进 textarea
function indent(tx) {

    tx.addEventListener("keydown", function (event) {

        // 回车
        if (event.keyCode === 13) {
            event.preventDefault();
            insertLine("\r\n");
        }

        // tab 键
        if (event.keyCode === 9) {
            event.preventDefault();
            insertTab("  ");
        }

    });

    // 插入回车，回车后保持上一行缩进
    function insertLine(v) {
        // 文本内容
        var txt = tx.value;
        // 光标位置
        var point = tx.selectionEnd;

        // hh 是否需要插入换行符
        // s 光标前的字符
        // e 光标后的字符
        // c 缩进的空格数 或者 换行+缩进空格数
        var s, e, c, hh = false;

        // 光标前是否存在回车
        // 1. 如果没有回车，说明是第一次回车
        // 2. 如果存在回车，取最近一次回车的位置 index
        var sn = txt.lastIndexOf('\n', point - 1);

        // 如果是第一次换行，设置换行为 true
        if (sn === -1) {
            hh = true;
        }

        // 取回车之后的所有字符，包含开头缩进的空格数
        var x1 = txt.substring(sn, txt.length);

        // 匹配开头的空格数作为缩进，或者匹配到 换行符+空格数，换行符也算是'空白'
        var rxx = /^\s*/gi;
        // 缩进的空格数
        c = x1.match(rxx)[0];

        // 光标前的字符
        s = txt.substring(0, point);
        // 光标后的字符
        e = txt.substring(point, txt.length);

        // 如果是第一次换行
        if (hh) {
            // 如果第一行就有缩进的话，所以换行的时候需要 +c
            txt = s + v + c + e;
        } else
            txt = s + c + e;

        tx.value = txt;

        // 设置光标位置
        if (hh) {
            // 第一次换行，需要增加一个 换行符的长度
            tx.setSelectionRange(point + c.length + 1, point + c.length + 1)
        } else {
            // 第二次以上换行，c 中包含换行符，所以长度不需要 +1
            tx.setSelectionRange(point + c.length, point + c.length)
        }
    }

    // 插入缩进
    function insertTab(v) {

        // 文本内容
        var txt = tx.value;
        // 光标位置
        var point = tx.selectionEnd;

        var frontPartString, endPartString;

        // 光标前文字
        frontPartString = txt.substring(0, point);
        // 光标后文字
        endPartString = txt.substring(point, txt.length);

        // 拼上 tab 空格数
        txt = frontPartString + v + endPartString;

        // 设置文本内容
        tx.value = txt;

        // 设置光标位置
        tx.setSelectionRange(point + v.length, point + v.length);
    }
}
