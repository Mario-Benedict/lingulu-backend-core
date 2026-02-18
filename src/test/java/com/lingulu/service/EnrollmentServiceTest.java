package com.lingulu.service;

import com.lingulu.entity.account.User;
import com.lingulu.entity.course.*;
import com.lingulu.enums.ProgressStatus;
import com.lingulu.exception.UserNotFoundException;
import com.lingulu.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @InjectMocks
    private EnrollmentService enrollmentService;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private SectionProgressRepository sectionProgressRepository;

    @Mock
    private LessonRepository lessonsRepository;

    @Mock
    private LessonProgressRepository lessonProgressRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseProgressRepository courseProgressRepository;

    @Mock
    private UserRepository userRepository;

    // TEST CASE 1
    @Test
    void enrollUserToAllLessons_userNotFound_shouldThrowException() {

        UUID userId = UUID.randomUUID();

        when(userRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                enrollmentService.enrollUserToAllLessons(userId));
    }


    // TEST CASE 2
    @Test
    void enrollUserToAllLessons_sectionProgress_shouldSave() {

        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setUserId(userId);

        Section section = new Section();

        when(userRepository.findByUserId(userId))
                .thenReturn(Optional.of(user));

        when(sectionRepository.findAll())
                .thenReturn(List.of(section));

        when(lessonsRepository.findAll())
                .thenReturn(Collections.emptyList());

        when(courseRepository.findAll())
                .thenReturn(Collections.emptyList());


        enrollmentService.enrollUserToAllLessons(userId);


        verify(sectionProgressRepository, times(1))
                .save(any(SectionProgress.class));
    }


    // TEST CASE 3
    @Test
    void enrollUserToAllLessons_lessonPosition1_shouldBeInProgress() {

        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setUserId(userId);

        Lesson lesson = new Lesson();
        lesson.setPosition(1);
        lesson.setLessonId(UUID.randomUUID());

        when(userRepository.findByUserId(userId))
                .thenReturn(Optional.of(user));

        when(sectionRepository.findAll())
                .thenReturn(Collections.emptyList());

        when(lessonsRepository.findAll())
                .thenReturn(List.of(lesson));

        when(sectionRepository.countByLesson_LessonId(any()))
                .thenReturn(1);

        when(courseRepository.findAll())
                .thenReturn(Collections.emptyList());


        enrollmentService.enrollUserToAllLessons(userId);


        ArgumentCaptor<LessonProgress> captor =
                ArgumentCaptor.forClass(LessonProgress.class);

        verify(lessonProgressRepository)
                .save(captor.capture());

        LessonProgress saved = captor.getValue();

        assertEquals(ProgressStatus.IN_PROGRESS,
                saved.getStatus());
    }


    // TEST CASE 4
    @Test
    void enrollUserToAllLessons_coursePosition1_shouldBeInProgress() {

        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setUserId(userId);

        Course course = new Course();
        course.setPosition(1);
        course.setCourseId(UUID.randomUUID());

        when(userRepository.findByUserId(userId))
                .thenReturn(Optional.of(user));

        when(sectionRepository.findAll())
                .thenReturn(Collections.emptyList());

        when(lessonsRepository.findAll())
                .thenReturn(Collections.emptyList());

        when(courseRepository.findAll())
                .thenReturn(List.of(course));

        when(lessonsRepository.countByCourse_CourseId(any()))
                .thenReturn(1);


        enrollmentService.enrollUserToAllLessons(userId);


        ArgumentCaptor<CourseProgress> captor =
                ArgumentCaptor.forClass(CourseProgress.class);

        verify(courseProgressRepository)
                .save(captor.capture());

        CourseProgress saved = captor.getValue();

        assertEquals(ProgressStatus.IN_PROGRESS,
                saved.getStatus());
    }



    // TEST CASE 5
    @Test
    void enrollUserToAllLessons_positionNot1_shouldBeNotStarted() {

        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setUserId(userId);

        Lesson lesson = new Lesson();
        lesson.setPosition(2);
        lesson.setLessonId(UUID.randomUUID());

        when(userRepository.findByUserId(userId))
                .thenReturn(Optional.of(user));

        when(sectionRepository.findAll())
                .thenReturn(Collections.emptyList());

        when(lessonsRepository.findAll())
                .thenReturn(List.of(lesson));

        when(sectionRepository.countByLesson_LessonId(any()))
                .thenReturn(1);

        when(courseRepository.findAll())
                .thenReturn(Collections.emptyList());


        enrollmentService.enrollUserToAllLessons(userId);


        ArgumentCaptor<LessonProgress> captor =
                ArgumentCaptor.forClass(LessonProgress.class);

        verify(lessonProgressRepository)
                .save(captor.capture());

        LessonProgress saved = captor.getValue();

        assertEquals(ProgressStatus.NOT_STARTED,
                saved.getStatus());
    }


}
