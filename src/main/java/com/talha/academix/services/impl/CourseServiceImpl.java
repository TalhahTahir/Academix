package com.talha.academix.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.CourseDTO;
import com.talha.academix.enums.ActivityAction;
import com.talha.academix.enums.CourseCategory;
import com.talha.academix.enums.CourseState;
import com.talha.academix.exception.AlreadyExistException;
import com.talha.academix.exception.ForbiddenException;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.exception.RoleMismatchException;
import com.talha.academix.model.Course;
import com.talha.academix.model.User;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.services.ActivityLogService;
import com.talha.academix.services.CourseService;
import com.talha.academix.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepo courseRepo;
    private final ModelMapper mapper;
    private final UserService userService;
    private final UserRepo userRepo;
    private final ActivityLogService activityLogService;

    /*----------------------------------- View Methods ----------------------------------- */

    @Override
    public CourseDTO getCourseById(Long id) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return mapper.map(course, CourseDTO.class);
    }

    @Override
    public List<CourseDTO> getCourseByCategory(CourseCategory category) {
        List<Course> courses = courseRepo.findAllByCategory(category);
        return courses.stream()
                .map(course -> mapper.map(course, CourseDTO.class))
                .toList();
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        List<Course> courses = courseRepo.findAll();
        return courses.stream()
                .map(course -> mapper.map(course, CourseDTO.class))
                .toList();
    }

    @Override
    public List<CourseDTO> getAllCoursesByTeacher(Long teacherId) {
        List<Course> courses = courseRepo.findAllByTeacherId(teacherId);
        return courses.stream()
                .map(course -> mapper.map(course, CourseDTO.class))
                .toList();
    }

    @Override
    public List<CourseDTO> getAllCoursesByState(CourseState state) {
        List<Course> courses = courseRepo.findAllByState(state);
        return courses.stream()
                .map(course -> mapper.map(course, CourseDTO.class))
                .toList();
    }

    /*----------------------------------- Action Methods ----------------------------------- */

    @Override
    public CourseDTO createCourseByTeacher(Long userid, CourseDTO dto) {
        if (userService.teacherValidation(userid) && Objects.equals(userid, dto.getTeacherid())) {
            return createCourse(dto);
        } else
            throw new RoleMismatchException("Only Teacher can create course");
    }

    @Override
    public CourseDTO updateCourseByAdmin(Long userid, Long courseId, CourseDTO dto) {
        if (userService.adminValidation(userid)) {
            return updateCourse(courseId, dto);
        } else
            throw new RoleMismatchException("Only Admin can update course");
    }

    @Override
    public void deleteCourseByTeacher(Long userid, Long courseId) {
        Boolean owned = teacherOwnership(userid, courseId);
        if (owned) {
            Course course = courseRepo.findById(courseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
            deleteCourse(courseId);
            activityLogService.logAction(userid, ActivityAction.COURSE_DELETION, null);
        } else
            throw new RoleMismatchException("Only Teacher can delete course");
    }

    @Override
    public void deleteCourseByAdmin(Long userid, Long courseId) {
        if (userService.adminValidation(userid)) {
            deleteCourse(courseId);
        } else
            throw new RoleMismatchException("Only Admin can delete course");
    }

    @Override
    public Boolean courseRejection(Long adminId, Long courseId) {

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!userService.adminValidation(adminId)) {
            throw new RoleMismatchException("Only Admin can reject course");
        }
        if (course.getState() != CourseState.DRAFT) {
            throw new ForbiddenException("Only courses in DRAFT state can be rejected");
        }
        course.setState(CourseState.REJECTED);
        courseRepo.save(course);
        activityLogService.logAction(
                adminId,
                ActivityAction.COURSE_REJECTED,
                course.getCoursename() + " of " + course.getTeacher().getUsername() + " has been rejected. ID = "
                        + courseId);
        return true;
    }

    @Override
    public Boolean courseApproval(Long adminId, Long courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!userService.adminValidation(adminId)) {
            throw new RoleMismatchException("Only Admin can approve course");
        }

        if (course.getState() == CourseState.DRAFT || course.getState() == CourseState.MODIFIED) {
            course.setState(CourseState.APPROVED);
            courseRepo.save(course);

            activityLogService.logAction(
                    adminId,
                    ActivityAction.COURSE_APPROVED,
                    course.getCoursename() + " of " + course.getTeacher().getUsername() + " has been approved. ID = "
                            + courseId);
            return true;
        } else {
            throw new ForbiddenException("Only DRAFTED or MODIFIED courses can be approved");
        }
    }

    @Override
    public CourseDTO courseModification(User teacher, Long courseId, CourseDTO dto) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        Boolean owned = teacherOwnership(teacher.getUserid(), courseId);

        if (owned && (course.getState() == CourseState.DRAFT
      || course.getState() == CourseState.MODIFIED
      || course.getState() == CourseState.REJECTED)) {

            CourseDTO updated = updateCourse(courseId, dto);

            activityLogService.logAction(teacher.getUserid(), ActivityAction.COURSE_MODIFIED, null);
            return updated;
        } else
            throw new ForbiddenException("Only courses in DRAFT, MODIFIED or REJECTED state can be modified");
    }

    @Override
    public Boolean courseDevelopment(User teacher, Long courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        Boolean owned = teacherOwnership(teacher.getUserid(), courseId);

        if (owned && course.getState() == CourseState.APPROVED) {
            course.setState(CourseState.IN_DEVELOPMENT);
            courseRepo.save(course);

            activityLogService.logAction(teacher.getUserid(), ActivityAction.COURSE_IN_DEVELOPMENT, null);
            return true;
        } else {
            throw new ForbiddenException("Only approved courses can be moved to IN_DEVELOPMENT state");
        }
    }

    @Override
    public Boolean courseLaunch(User teacher, Long courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        Boolean owned = teacherOwnership(teacher.getUserid(), courseId);
        if (owned && course.getState() == CourseState.IN_DEVELOPMENT) {
            course.setState(CourseState.LAUNCHED);
            courseRepo.save(course);

            activityLogService.logAction(teacher.getUserid(), ActivityAction.COURSE_LAUNCHED, null);
            return true;
        } else {
            throw new ForbiddenException("Only courses in IN_DEVELOPMENT state can be launched");
        }
    }

    @Override
    public CourseDTO courseDisable(Long adminId, Long courseId) {
        if (!userService.adminValidation(adminId)) {
            throw new RoleMismatchException("Only Admin can disable course");
        }
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (course.getState() == CourseState.LAUNCHED) {
            course.setState(CourseState.DISABLED);
            courseRepo.save(course);

            activityLogService.logAction(
                    adminId,
                    ActivityAction.COURSE_DISABLED,
                    course.getCoursename() + " of " + course.getTeacher().getUsername() + " has been disabled. ID = "
                            + courseId);
            return mapper.map(course, CourseDTO.class);
        } else {
            throw new ForbiddenException("Only launched courses can be disabled");
        }
    }

    /*----------------------------------- Inner Methods ----------------------------------- */

    @Override
    public CourseDTO createCourse(CourseDTO dto) {
        if (!courseRepo.existsByCoursenameAndTeacherId(dto.getCoursename(), dto.getTeacherid())) {
            Course course = new Course();
            course.setCoursename(dto.getCoursename());
            course.setDuration(dto.getDuration());
            course.setFees(dto.getFees());
            course.setCategory(dto.getCategory());
            User teacher = userRepo.findById(dto.getTeacherid())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + dto.getTeacherid()));
            course.setTeacher(teacher);
            course.setState(CourseState.DRAFT);
            course = courseRepo.save(course);

            activityLogService.logAction(dto.getTeacherid(), ActivityAction.COURSE_DRAFTED, null);

            return mapper.map(course, CourseDTO.class);
        } else
            throw new AlreadyExistException("Teacher already have a course with name: " + dto.getCoursename());
    }

    @Override
    public CourseDTO updateCourse(Long courseId, CourseDTO dto) {
        Course existing = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        existing.setCoursename(dto.getCoursename());
        existing.setDuration(dto.getDuration());
        existing.setFees(dto.getFees());
        existing.setCategory(dto.getCategory());
        existing.setState(CourseState.MODIFIED);
        existing = courseRepo.save(existing);
        return mapper.map(existing, CourseDTO.class);
    }

    @Override
    public void deleteCourse(Long id) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        courseRepo.delete(course);
    }

    @Override
    public boolean teacherOwnership(Long userid, Long courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        return course.getTeacher() != null && course.getTeacher().getUserid().equals(userid);
    }
}
