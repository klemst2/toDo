package com.example.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NoteController {

	private static final String LIST_OF_NOTES = "listOfNotes";

	@Autowired
	NoteRepository noteRepository;

	@GetMapping("/addNote")
	String addNote(Model model) {

		Note note = new Note();
		model.addAttribute("note", note);
		return "addNote";
	}

	@PostMapping("/addNote")
	String addNote(@Valid Note note, BindingResult bindingResult) {
		if (bindingResult.hasErrors())
			return "addNote";

		noteRepository.save(note);
		return "redirect:/" + LIST_OF_NOTES;

	}

	@GetMapping(LIST_OF_NOTES)
	String addUser(Model model, @RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "addToDone", required = false) Optional<Integer> idDone,
			@RequestParam(value = "search", required = false) String search) {

		Optional<Integer> idAddDone = idDone;
		if (idAddDone.isPresent() && (idAddDone.get() > 0)) {

			Optional<Note> n = noteRepository.findById(idAddDone.get().longValue());
			if (n.isPresent()) {
				n.get().setDone(true);
				noteRepository.save(n.get());
			}
		}

		List<Note> searchList = new ArrayList<>();
		if (search != null) {
			searchList = noteRepository.findAllByTitleAndDescription(search);
		}

		List<Note> listToDo = new ArrayList<>();
		listToDo = noteRepository.findAllByDone(false);

		List<Note> listDone = new ArrayList<>();
		listDone = noteRepository.findAllByDone(true);

		if (sort != null) {
			if (sort.equals("sortById"))
				listToDo.sort(Comparator.comparing(Note::getId));

			else if (sort.equals("sortByTitle"))
				listToDo.sort(Comparator.comparing(Note::getTitle));

			else if (sort.equals("sortByDescription"))
				listToDo.sort(Comparator.comparing(Note::getDescription));
			else if (sort.equals("sortData"))
				listToDo.sort(Comparator.comparing(Note::getTimestamp));
			else if (sort.equals("sortRandom"))
				Collections.shuffle(listToDo);
		}

		String infoListDone = "The list of done is EMPTY";
		if (!listDone.isEmpty())
			infoListDone = "The list of done";

		String infoListToDo = "The list to do is EMPTY";
		if (!listToDo.isEmpty())
			infoListToDo = "The list to do";

		boolean isEmptyToDo = true;
		if (!listToDo.isEmpty())
			isEmptyToDo = false;

		model.addAttribute("searchList", searchList);
		model.addAttribute("isEmptyToDo", isEmptyToDo);
		model.addAttribute("infoListToDo", infoListToDo);
		model.addAttribute("infoListDone", infoListDone);
		model.addAttribute("listDone", listDone);
		model.addAttribute("listToDo", listToDo);
		return LIST_OF_NOTES;
	}

	@GetMapping("/undo/{id}")
	String undo(@PathVariable long id) {
		Optional<Note> n = noteRepository.findById(id);
		if (n.isPresent()) {
			n.get().setDone(false);
			noteRepository.save(n.get());
		}

		return "redirect:/" + LIST_OF_NOTES;
	}

	@GetMapping("/edit/{id}")
	String editGet(@PathVariable long id, Model model) {
		Optional<Note> note = noteRepository.findById(id);
		if (note.isPresent()) {
			model.addAttribute("note", note.get());
		}

		return "edit";
	}

	@PostMapping("/edit/{id}")
	String editPost(@PathVariable long id, @Valid Note noteEdit, BindingResult bindingResult) {

		if (bindingResult.hasErrors())
			return "edit";

		Optional<Note> note = noteRepository.findById(id);
		if (note.isPresent()) {
			note.get().setTitle(noteEdit.getTitle());
			note.get().setDescription(noteEdit.getDescription());
			noteRepository.save(note.get());
		}

		return "redirect:/" + LIST_OF_NOTES;

	}

	@GetMapping("/delete/{id}")
	String delete(@PathVariable long id) {
		Optional<Note> note = noteRepository.findById(id);
		if (note.isPresent()) {
			noteRepository.delete(note.get());
		}

		return "redirect:/" + LIST_OF_NOTES;
	}

	@GetMapping("/deleteAll")
	String deleteAll() {
		noteRepository.deleteAll();
		return "redirect:/" + LIST_OF_NOTES;
	}

	@GetMapping("/addRandom")
	String addRandom() {

		Random random = new Random();

		for (int i = 0; i < 5; i++) {

			StringBuilder title = new StringBuilder();

			for (int j = 0; j < random.nextInt(15) + 5; j++) {
				int tmp = random.nextInt(26) + 97;
				title.append((char) tmp);
			}

			StringBuilder description = new StringBuilder();
			for (int k = 0; k < random.nextInt(20) + 7; k++) {
				int tmp = random.nextInt(26) + 97;
				 description.append((char) tmp);
			}

			Note n = new Note(title.toString(), description.toString());
			noteRepository.save(n);
		}
		return "redirect:/" + LIST_OF_NOTES;
	}

}
