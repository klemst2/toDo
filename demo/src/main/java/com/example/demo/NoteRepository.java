package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

	@Query("SELECT n FROM Note n WHERE n.isDone = ?1")
	public List<Note> findAllByDone(boolean isDone);

	@Query("SELECT n FROM Note n WHERE n.title LIKE %?1% OR n.description LIKE %?1%")
	public List<Note> findAllByTitleAndDescription(String q);
}
