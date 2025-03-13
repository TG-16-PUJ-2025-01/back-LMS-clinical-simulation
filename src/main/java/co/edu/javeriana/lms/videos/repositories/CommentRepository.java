package co.edu.javeriana.lms.videos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.javeriana.lms.videos.models.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {}
