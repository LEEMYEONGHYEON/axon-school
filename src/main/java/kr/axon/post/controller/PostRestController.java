package kr.axon.post.controller;

import kr.axon.post.command.domain.PostContent;
import kr.axon.post.command.domain.PostIdentifier;
import kr.axon.post.controller.hateoas.PostResource;
import kr.axon.post.controller.hateoas.PostResourceAssembler;
import kr.axon.post.controller.hateoas.PostResourceLinks;
import kr.axon.post.exception.PostNotFoundException;
import kr.axon.post.query.model.Post;
import kr.axon.post.query.repository.PostQueryRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static kr.axon.post.command.api.PostCommand.*;
import static kr.axon.supporter.Supporters.httpHeadersWithLocation;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private PostQueryRepository repository;

    @Autowired
    private PostResourceAssembler resourceAssembler;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public HttpHeaders save(@RequestBody PostContent content) {
        PostIdentifier id = new PostIdentifier();
        CreatePostCommand command = new CreatePostCommand(id, content);
        commandGateway.sendAndWait(command);
        return httpHeadersWithLocation(PostResourceLinks.getPostUri(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public HttpHeaders modify(@PathVariable PostIdentifier id,
                              @RequestBody PostContent content) {
        ModifyPostCommand command = new ModifyPostCommand(id, content);
        commandGateway.sendAndWait(command);
        return httpHeadersWithLocation(PostResourceLinks.getPostUri(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public HttpHeaders delete(@PathVariable PostIdentifier id) {
        DeletePostCommand command = new DeletePostCommand(id);
        commandGateway.sendAndWait(command);
        return new HttpHeaders();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public PostResource getPost(@PathVariable PostIdentifier id) {
        Optional<Post> post = repository.findById(id);
        post.orElseThrow(() -> new PostNotFoundException(id));
        return resourceAssembler.toResource(post.get());
    }

    @RequestMapping(method = RequestMethod.GET)
    public PagedResources<PostResource> showAll(@PageableDefault Pageable pageable) {
        Page<Post> posts = repository.findAll(pageable);
        return resourceAssembler.toResources(posts);
    }
}
