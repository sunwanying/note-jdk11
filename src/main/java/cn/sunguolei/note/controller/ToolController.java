package cn.sunguolei.note.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author GuoLei Sun
 * Date: 2018/12/7 2:53 PM
 */
@Controller
@RequestMapping("/tool")
public class ToolController {

    @GetMapping("/jsonToParam")
    public String jsonToParam() {
        return "tool/jsonToParam";
    }
}
