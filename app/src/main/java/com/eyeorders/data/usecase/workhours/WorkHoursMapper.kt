package com.eyeorders.data.usecase.workhours

import androidx.annotation.StringRes
import com.eyeorders.data.remote.api.response.WorkHoursResponse
import com.eyeorders.data.usecase.orders.mapper.Mapper
import com.eyeorders.screens.workhours.WorkHours
import com.eyeorders.util.StringResource
import com.tasleem.orders.R
import javax.inject.Inject

class WorkHoursMapper @Inject constructor(
    private val stringResource: StringResource,
) : Mapper<WorkHoursResponse, List<WorkHours>> {

    override fun map(entity: WorkHoursResponse): List<WorkHours> {
        val workHours = mutableListOf<WorkHours>()
        workHours.apply {
            add(
                WorkHours(
                    1,
                    R.string.saturday,
                    getString(
                        R.string.work_hours_start_end,
                        entity.satStart,
                        entity.satEnd
                    )
                )
            )

            add(
                WorkHours(
                    2,
                    R.string.sunday,
                    getString(
                        R.string.work_hours_start_end,
                        entity.sunStart,
                        entity.sunEnd
                    )
                )
            )

            add(
                WorkHours(
                    3,
                    R.string.monday,
                    getString(
                        R.string.work_hours_start_end,
                        entity.monStart,
                        entity.monEnd
                    )
                )
            )

            add(
                WorkHours(
                    4,
                    R.string.tuesday,
                    getString(
                        R.string.work_hours_start_end,
                        entity.tueStart,
                        entity.tueEnd
                    )
                )
            )

            add(
                WorkHours(
                    5,
                    R.string.wednesday,
                    getString(
                        R.string.work_hours_start_end,
                        entity.wedStart,
                        entity.wedEnd
                    )
                )
            )

            add(
                WorkHours(
                    6,
                    R.string.thursday,
                    getString(
                        R.string.work_hours_start_end,
                        entity.thuStart,
                        entity.thuEnd
                    )
                )
            )

            add(
                WorkHours(
                    7,
                    R.string.friday,
                    getString(
                        R.string.work_hours_start_end,
                        entity.friStart,
                        entity.friEnd
                    )
                )
            )
        }
        return workHours
    }

    private fun getString(@StringRes resId: Int, vararg args: Any): String {
        return stringResource.getString(resId, *args)
    }
}