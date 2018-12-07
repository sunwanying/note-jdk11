package cn.sunguolei.note.service;

import cn.sunguolei.note.entity.Note;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface NoteService {
    List<Note> listAll();

    List<Note> index(int userId);

    List<Note> findByName(int userId, String keyword);

    PageInfo<Note> homeNoteList(int pageNum, int pageSize);

    int create(Note note);

    Note findNoteById(int id);

    int update(Note note);
}
