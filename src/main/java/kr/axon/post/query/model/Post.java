package kr.axon.post.query.model;

import kr.axon.post.command.domain.PostContent;
import kr.axon.post.command.domain.PostIdentifier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @EmbeddedId
    private PostIdentifier id;

    @Embedded
    private PostContent content;
}
