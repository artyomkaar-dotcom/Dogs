package com.kodex.guide.domain.usecase

import com.kodex.guide.data.mapper.DraftMapper
import com.kodex.guide.domain.model.PostEditorState
import com.kodex.guide.ui.db.DraftDao
 import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class ObserveLatestDraftUseCase @Inject constructor(
    private val draftDao: DraftDao
) {
    operator fun invoke(): Flow<PostEditorState?> =
        draftDao.getLatestDraft().map { it?.let(DraftMapper::toState) }
}