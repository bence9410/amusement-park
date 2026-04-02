package hu.beni.amusementpark.mapper;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public abstract class EntityMapper<T, R extends RepresentationModel<R>>
        extends RepresentationModelAssemblerSupport<T, R> {

    private final PagedResourcesAssembler<T> pagedResourcesAssembler;

    protected <C> EntityMapper(Class<C> controllerClass, Class<R> resourceClass,
                               PagedResourcesAssembler<T> pagedResourcesAssembler) {
        super(controllerClass, resourceClass);
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    public PagedModel<R> toPagedResources(Page<T> page) {
        return pagedResourcesAssembler.toModel(page, this);
    }

    public abstract T toEntity(R resource);

}
