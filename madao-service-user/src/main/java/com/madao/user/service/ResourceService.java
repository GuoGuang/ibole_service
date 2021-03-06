package com.madao.user.service;

import com.madao.exception.custom.ResourceNotFoundException;
import com.madao.model.QueryVO;
import com.madao.model.dto.user.ResourceDto;
import com.madao.model.pojo.user.Resource;
import com.madao.model.pojo.user.RoleResource;
import com.madao.redis.RedisService;
import com.madao.user.dao.ResourceDao;
import com.madao.user.dao.RoleResourceDao;
import com.madao.user.mapper.ResourceMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author GuoGuang
 * @公众号 码道人生
 * @gitHub https://github.com/GuoGuang
 * @website https://madaoo.com
 * @created 2019-09-29 7:37
 */
@Service
public class ResourceService {

    private final ResourceDao resourceDao;
    private final ResourceMapper resourceMapper;
    private final RoleResourceDao roleResourceDao;
	private final RedisService redisService;

    @Autowired
    public ResourceService(ResourceDao resourceDao,
                           RoleResourceDao roleResourceDao,
                           ResourceMapper resourceMapper, RedisService redisService) {
        this.resourceDao = resourceDao;
        this.roleResourceDao = roleResourceDao;
        this.resourceMapper = resourceMapper;
	    this.redisService = redisService;
    }

    /**
     * 条件查询资源
     *
     * @param resourceDto 资源实体
     * @param queryVO     查询参数
     * @return List
     */
    public List<ResourceDto> findResourceByCondition(ResourceDto resourceDto, QueryVO queryVO) {

        Specification<Resource> condition = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotEmpty(resourceDto.getName())) {
                predicates.add(builder.like(root.get("name"), "%" + resourceDto.getName() + "%"));
            }
            Predicate[] ps = new Predicate[predicates.size()];
            return query.where(builder.and(predicates.toArray(ps))).getRestriction();
        };
        List<Resource> result = resourceDao.findAll(condition);
        return resourceMapper.toDto(result);
    }

    public ResourceDto findResourceById(String resId) {
        return resourceDao.findById(resId)
                .map(resourceMapper::toDto)
                .orElseThrow(ResourceNotFoundException::new);
    }

    public Set<ResourceDto> findResourceByRoleIds(List<String> resId) {
        return Optional.ofNullable(resourceDao.findResourceByRoleIds(resId))
                .map(resourceMapper::toDto)
                .orElseThrow(ResourceNotFoundException::new);
    }

    public void saveOrUpdate(ResourceDto resourceDto) {
        resourceDao.save(resourceMapper.toEntity(resourceDto));

        List<RoleResource> roleResources = resourceDto.getRoles().stream()
                .map(resource -> new RoleResource(resource.getId(), resourceDto.getId()))
                .collect(Collectors.toList());

        roleResourceDao.deleteByRoleIdIn(Collections.singletonList(resourceDto.getId()));
        roleResourceDao.saveAll(roleResources);
	    redisService.del("resources");
    }

    public void deleteByIds(List<String> resId) {
        resourceDao.deleteBatch(resId);
        roleResourceDao.deleteByResourceIdIn(resId);
	    redisService.del("resources");
    }
}
