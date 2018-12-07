package cn.sunguolei.note.mapper;

import cn.sunguolei.note.entity.Note;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoteMapper {
    List<Note> listAll();

    List<Note> index(int userId);

    List<Note> homeNoteList();

    int create(Note note);

    Note findNoteById(int id);

    int update(Note note);

    List<Note> findByName(int userId, String keyword);
}
