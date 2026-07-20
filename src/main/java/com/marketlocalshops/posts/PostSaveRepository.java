package com.marketlocalshops.posts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PostSaveRepository extends JpaRepository<PostSave, Long> {
    Optional<PostSave> findByPost_IdAndUser_Id(Long postId, Long userId);
}
