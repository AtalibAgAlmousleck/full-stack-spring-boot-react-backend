package com.atalibdev.service;

import com.atalibdev.entity.Student;
import com.atalibdev.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.atalibdev.constants.Constant.PHOTO_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public Page<Student> getAllStudents(int page, int size) {
        return studentRepository.findAll(PageRequest.of(page, size, Sort.by("name")));
    }

    public Student getStudent(String id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public void deleteStudent(String id) {

    }

    public String uploadPhoto(String id, MultipartFile file) {
        log.info("Saving picture for user with the id: {}", id);
        Student student = getStudent(id);
        String photoUrl = photoFunction.apply(id, file);
        student.setPhotoUrl(photoUrl);
        studentRepository.save(student);
        return photoUrl;
    }

    private final Function<String, String> fileExtension = fileName -> Optional.of(fileName)
            .filter(name -> name.contains(".")).map(name -> "." + name.substring(fileName.lastIndexOf(".") + 1))
            .orElse(".png");

    private final BiFunction<String, MultipartFile, String> photoFunction = (id, image) -> {
        String filename = id + fileExtension.apply(image.getOriginalFilename());
        try {
            Path fileStorageLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();
            if (!Files.exists(fileStorageLocation))
                Files.createDirectories(fileStorageLocation);
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(filename), REPLACE_EXISTING);
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/students/image/" + filename).toUriString();
        }catch (Exception ex) {
            throw new RuntimeException("Unable to save image");
        }
    };
}
