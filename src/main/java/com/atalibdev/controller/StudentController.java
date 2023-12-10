package com.atalibdev.controller;

import com.atalibdev.entity.Student;
import com.atalibdev.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.atalibdev.constants.Constant.PHOTO_DIRECTORY;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        return ResponseEntity.created(URI.create("/students/userID"))
                .body(studentService.createStudent(student));
    }

    @GetMapping
    public ResponseEntity<Page<Student>> getStudents(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "8") int size) {
        return ResponseEntity.ok().body(studentService.getAllStudents(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok().body(studentService.getStudent(id));
    }

    @PutMapping("/photo")
    public ResponseEntity<String> uploadPhoto(@RequestParam("id") String id,
                                              @RequestParam("file")MultipartFile file) {
        return ResponseEntity.ok().body(studentService.uploadPhoto(id, file));
    }

    @GetMapping(path = "/image/{filename}", produces = { IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE })
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(PHOTO_DIRECTORY + filename));
    }
}
