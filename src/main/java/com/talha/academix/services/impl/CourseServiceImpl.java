package com.talha.academix.services.impl;

import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.talha.academix.dto.CourseDTO;
import com.talha.academix.dto.CourseViewDTO;
import com.talha.academix.dto.CreateCourseDTO;
import com.talha.academix.enums.CourseBadge;
import com.talha.academix.enums.CourseCategory;
import com.talha.academix.enums.CourseState;
import com.talha.academix.enums.EnrollmentStatus;
import com.talha.academix.exception.AlreadyExistException;
import com.talha.academix.exception.ForbiddenException;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.mapper.CourseMapper;
import com.talha.academix.model.Course;
import com.talha.academix.model.User;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.repository.EnrollmentRepo;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.services.CourseService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepo courseRepo;
    private final UserRepo userRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final CourseMapper courseMapper;
    /*----------------------------------- View Methods ----------------------------------- */

    @Override
    public CourseDTO getCourseById(Long id) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return courseMapper.toDto(course);
    }

    @Override
    public List<CourseDTO> getCourseByCategory(CourseCategory category) {
        List<Course> courses = courseRepo.findAllByCategory(category);
        return courses.stream()
                .map(courseMapper::toDto)
                .toList();
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        List<Course> courses = courseRepo.findAll();
        return courses.stream()
                .map(courseMapper::toDto)
        .toList();
    }

    @Override
    public List<CourseDTO> getAllCoursesByTeacher(Long teacherId) {
        List<Course> courses = courseRepo.findAllByTeacher_Userid(teacherId);
        return courses.stream()
                .map(courseMapper::toDto)
                .toList();
    }

    @Override
    public List<CourseDTO> getAllCoursesByState(CourseState state) {
        List<Course> courses = courseRepo.findAllByState(state);
        return courses.stream()
                .map(courseMapper::toDto)

                .toList();
    }

    @Override
    public List<CourseViewDTO> viewAllCourses(Long studentId) {
        List<Course> courses = courseRepo.findAll();
        List<CourseViewDTO> coursesview = new ArrayList<>();
        courses.forEach(course -> {
            CourseViewDTO courseViewDTO = courseMapper.toViewDto(course);
            if (enrollmentRepo.existsByStudent_UseridAndCourse_CourseId(studentId, course.getCourseId())) {
                courseViewDTO.setBadge(CourseBadge.Enrolled);
            }
            enrollmentRepo.findEnrollmentStatusByStudent_UseridAndCourse_CourseId(studentId, course.getCourseId())
                    .filter(status -> status == EnrollmentStatus.COMPLETED)
                    .ifPresent(s -> courseViewDTO.setBadge(CourseBadge.Completed));
            coursesview.add(courseViewDTO);
        });
        return coursesview;
    }

    /*----------------------------------- Action Methods ----------------------------------- */

    // @Override
    // public CourseDTO createCourseByTeacher(Long userid, CourseDTO dto) {
    // if (userService.teacherValidation(userid) && Objects.equals(userid,
    // dto.getTeacherid())) {
    // return createCourse(dto);
    // } else
    // throw new RoleMismatchException("Only Teacher can create course");
    // }

    // @Override
    // public CourseDTO updateCourseByAdmin(Long userid, Long courseId, CourseDTO
    // dto) {
    // if (userService.adminValidation(userid)) {
    // return updateCourse(courseId, dto);
    // } else
    // throw new RoleMismatchException("Only Admin can update course");
    // }

    // @Override
    // public void deleteCourseByTeacher(Long userid, Long courseId) {
    // Boolean owned = teacherOwnership(userid, courseId);
    // if (owned) {
    // Course course = courseRepo.findById(courseId)
    // .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: "
    // + courseId));
    // deleteCourse(courseId);
    // } else
    // throw new RoleMismatchException("Only Teacher can delete course");
    // }

    // @Override
    // public void deleteCourseByAdmin(Long userid, Long courseId) {
    // if (userService.adminValidation(userid)) {
    // deleteCourse(courseId);
    // } else
    // throw new RoleMismatchException("Only Admin can delete course");
    // }

    @Override
    public CourseDTO courseRejection(Long courseId) {

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (course.getState() != CourseState.DRAFT) {
            throw new ForbiddenException("Only courses in DRAFT state can be rejected");
        }
        course.setState(CourseState.REJECTED);
        courseRepo.save(course);
        return courseMapper.toDto(course);
    }

    @Override
    public CourseDTO courseApproval(Long courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (course.getState() == CourseState.DRAFT || course.getState() == CourseState.MODIFIED) {
            course.setState(CourseState.APPROVED);
            courseRepo.save(course);

            return courseMapper.toDto(course);
        } else {
            throw new ForbiddenException("Only DRAFTED or MODIFIED courses can be approved");
        }
    }

    @Override
    public CourseDTO courseModification(Long courseId, CourseDTO dto) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if ( (course.getState() == CourseState.DRAFT
                || course.getState() == CourseState.MODIFIED
                || course.getState() == CourseState.REJECTED)) {

            CourseDTO updated = updateCourse(courseId, dto);

            return updated;
        } else
            throw new ForbiddenException("Only courses in DRAFT, MODIFIED or REJECTED state can be modified");
    }

    @Override
    public CourseDTO courseDevelopment(Long courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (course.getState() == CourseState.APPROVED) {
            course.setState(CourseState.IN_DEVELOPMENT);
            courseRepo.save(course);

            return courseMapper.toDto(course);
        } else {
            throw new ForbiddenException("Only approved courses can be moved to IN_DEVELOPMENT state");
        }
    }

    @Override
    public CourseDTO courseLaunch(Long courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (course.getState() == CourseState.IN_DEVELOPMENT) {
            course.setState(CourseState.LAUNCHED);
            courseRepo.save(course);

            return courseMapper.toDto(course);
        } else {
            throw new ForbiddenException("Only courses in IN_DEVELOPMENT state can be launched");
        }
    }

    @Override
    public CourseDTO courseDisable(Long courseId) {

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (course.getState() == CourseState.LAUNCHED) {
            course.setState(CourseState.DISABLED);
            courseRepo.save(course);

            return courseMapper.toDto(course);
        } else {
            throw new ForbiddenException("Only launched courses can be disabled");
        }
    }

    /*----------------------------------- Inner Methods ----------------------------------- */

    @Override
    public CourseDTO createCourse(CreateCourseDTO cdto) {
        if (!courseRepo.existsByCourseNameAndTeacher_Userid(cdto.getCourseName(), cdto.getTeacherId())) {
            Course course = new Course();
            course.setCourseName(cdto.getCourseName());
            course.setDuration(cdto.getDuration());
            course.setFees(cdto.getFees());
            course.setCategory(cdto.getCategory());
            User teacher = userRepo.findById(cdto.getTeacherId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Teacher not found with id: " + cdto.getTeacherId()));
            course.setTeacher(teacher);
            course.setState(CourseState.DRAFT);
            course = courseRepo.save(course);

            return courseMapper.toDto(course);

        } else
            throw new AlreadyExistException("Teacher already have a course with name: " + cdto.getCourseName());
    }

    @Override
    public CourseDTO updateCourse(Long courseId, CourseDTO dto) {
        Course existing = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        courseMapper.updateCourseFromDto(dto, existing);
        existing.setState(CourseState.MODIFIED);
        existing = courseRepo.save(existing);
        return courseMapper.toDto(existing);
    }

    @Override
    public void deleteCourse(Long id) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        courseRepo.delete(course);
    }

    @Override
    public Long countAll() {
        return courseRepo.count();
    }
}
