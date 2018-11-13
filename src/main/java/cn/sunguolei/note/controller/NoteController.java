package cn.sunguolei.note.controller;

import cn.sunguolei.note.entity.Note;
import cn.sunguolei.note.entity.User;
import cn.sunguolei.note.service.NoteService;
import cn.sunguolei.note.service.UserService;
import cn.sunguolei.note.utils.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * @author GuoLei Sun
 * Date: 2018/11/9 11:22 AM
 */
@Controller
@RequestMapping("/note")
public class NoteController {
    private Logger logger = LoggerFactory.getLogger(NoteController.class);

    private NoteService noteService;
    private UserService userService;

    // 将用到的 service 注入进来
    public NoteController(NoteService noteService, UserService userService) {
        this.noteService = noteService;
        this.userService = userService;
    }

    /**
     * 笔记的列表页，默认加载登录用户的笔记
     *
     * @param request http 请求
     * @param model   存放前端数据的 model
     * @return 笔记列表页或者登录页面
     */
    @GetMapping("/index")
    public String index(HttpServletRequest request, Model model) {
        // 获取请求 cookie 中的 token
        var token = UserUtil.getTokenFromCookie(request);

        if (token.isPresent()) {
            // 获取用户登录信息和用户信息
            Map<String, String> userInfoMap = UserUtil.getUserIdentity(request);
            // 通过用户名查找对应的用户
            User user = userService.findUserByUsername(userInfoMap.get("username"));
            // 通过用户 id 查找对应的用户的笔记
            var noteList = noteService.index(user.getId());
            // 将笔记列表存放到 model 中，返回给前端页面
            model.addAttribute("noteList", noteList);

            return "note/index";
        } else {
            // 如果找不到 token，就返回 登录 页面
            return "redirect:/toLogin";
        }
    }

    /**
     * 打开写笔记页面
     *
     * @return 返回对应页面
     */
    @GetMapping("/add")
    public String add() {
        return "note/add";
    }

    /**
     * 创建笔记
     *
     * @param note 从表单中获取笔记信息
     * @return 返回笔记列表页
     */
    @PostMapping("/create")
    public String create(HttpServletRequest request, Note note) {

        // 获取请求 cookie 中的 token
        Optional<String> token = UserUtil.getTokenFromCookie(request);

        if (token.isPresent()) {
            // 获取用户登录信息和用户信息
            Map<String, String> userInfoMap = UserUtil.getUserIdentity(request);
            // 通过用户名查找对应的用户
            User user = userService.findUserByUsername(userInfoMap.get("username"));
            note.setUserId(user.getId());
        }
        // 笔记创建时间
        LocalDateTime createTime = LocalDateTime.now();
        note.setCreateTime(createTime);

        // 调用 service 创建笔记
        int number = noteService.create(note);

        if (number > 0) {
            logger.debug("笔记创建成功");
        }

        // 返回笔记列表页
        return "redirect:/note/index";
    }

    /**
     * 根据ID打开笔记页面
     *
     * @return 返回对应页面
     */
    @GetMapping("/view/{id}")
    public String view(@PathVariable("id") int id, Model model) {
        Note note = noteService.findNoteById(id);

        // 如果笔记是隐藏的
        if (note.getType() == 1) {
            String username = (String) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            User user = userService.findUserByUsername(username);
            if (user == null) {
                return "redirect:/toLogin";
            } else {
                // 当前登录的用户 ID
                int userId = user.getId();
                // 笔记中记录的创建者的 ID
                int noteUserId = note.getUserId();
                // 只有两者相等才能查看笔记
                if (userId != noteUserId) {
                    return "redirect:/note/index";
                }
            }
        }

        model.addAttribute("note", note);

        return "note/view";
    }

    /**
     * 打开更新笔记页面
     *
     * @return 返回对应页面
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model) {
        String username = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        User user = userService.findUserByUsername(username);
        // 当前登录的用户 ID
        int userId = user.getId();
        // 笔记中记录的创建者的 ID
        Note note = noteService.findNoteById(id);
        int noteUserId = note.getUserId();
        // 只有两者相等才能编辑笔记
        if (userId == noteUserId) {
            model.addAttribute("note", note);

            return "note/edit";
        } else {
            return "redirect:/note/index";
        }
    }

    /**
     * 更新笔记
     *
     * @param note 从表单中获取笔记信息
     * @return 重定向到更新后的笔记
     */
    @PostMapping("/update")
    public String update(Note note) {

        String username = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        User user = userService.findUserByUsername(username);
        // 当前登录的用户 ID
        int userId = user.getId();
        // 笔记中记录的创建者的 ID
        int noteUserId = noteService.findNoteById(note.getId()).getUserId();
        // 只有两者相等才能编辑笔记
        if (userId == noteUserId) {
            // 调用 service 更新笔记
            int number = noteService.update(note);

            if (number > 0) {
                logger.debug("笔记更新成功");
            }
        }

        // 返回笔记列表页
        return "redirect:/note/view/" + note.getId();
    }
}
