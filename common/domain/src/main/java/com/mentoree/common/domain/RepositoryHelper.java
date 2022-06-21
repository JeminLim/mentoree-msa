package com.mentoree.common.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

public class RepositoryHelper {

    public static <T> Slice<T> toSlice(final List<T> contents, final Pageable pageable) {
        final boolean hasNext = pageable.isPaged() && contents.size() > pageable.getPageSize();
        return new SliceImpl<>(hasNext ? contents.subList(0, pageable.getPageSize()) : contents, pageable, hasNext);
    }

}
