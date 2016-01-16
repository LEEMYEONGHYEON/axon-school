package kr.axon.post.controller.hateoas;

import kr.axon.post.command.domain.PostIdentifier;
import kr.axon.post.controller.PostRestController;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class PostResourceLinks {

    private static final Class<PostRestController> controllerClass = PostRestController.class;

    public static ControllerLinkBuilder location(PostIdentifier id) {
        return linkTo(controllerClass).slash(id);
    }
}
