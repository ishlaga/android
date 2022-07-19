/**
 * ownCloud Android client application
 *
 * @author Juan Carlos Garrote Gascón
 *
 * Copyright (C) 2022 ownCloud GmbH.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.owncloud.android.presentation.viewmodels.transfers

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owncloud.android.domain.transfers.model.OCTransfer
import com.owncloud.android.domain.transfers.usecases.GetAllTransfersUseCase
import com.owncloud.android.domain.utils.Event
import com.owncloud.android.presentation.UIResult
import com.owncloud.android.providers.CoroutinesDispatcherProvider
import com.owncloud.android.usecases.transfers.uploads.UploadFilesFromSAFUseCase
import com.owncloud.android.usecases.transfers.uploads.UploadFilesFromSystemUseCase
import kotlinx.coroutines.launch

class TransfersViewModel(
    private val uploadFilesFromSAFUseCase: UploadFilesFromSAFUseCase,
    private val uploadFilesFromSystemUseCase: UploadFilesFromSystemUseCase,
    private val getAllTransfersUseCase: GetAllTransfersUseCase,
    private val coroutinesDispatcherProvider: CoroutinesDispatcherProvider,
) : ViewModel() {

    private val _transfersListLiveData = MediatorLiveData<List<OCTransfer>>()
    val transfersListLiveData: LiveData<List<OCTransfer>>
        get() = _transfersListLiveData

    private var transfersLiveData = getAllTransfersUseCase.execute(Unit)

    init {
        _transfersListLiveData.addSource(transfersLiveData) { transfers ->
            _transfersListLiveData.postValue(transfers)
        }
    }

    fun uploadFilesFromSAF(
        accountName: String,
        listOfContentUris: List<Uri>,
        uploadFolderPath: String
    ) {
        viewModelScope.launch(coroutinesDispatcherProvider.io) {
            uploadFilesFromSAFUseCase.execute(
                UploadFilesFromSAFUseCase.Params(
                    accountName = accountName,
                    listOfContentUris = listOfContentUris,
                    uploadFolderPath = uploadFolderPath
                )
            )
        }
    }

    fun uploadFilesFromSystem(
        accountName: String,
        listOfLocalPaths: List<String>,
        uploadFolderPath: String
    ) {
        viewModelScope.launch(coroutinesDispatcherProvider.io) {
            uploadFilesFromSystemUseCase.execute(
                UploadFilesFromSystemUseCase.Params(
                    accountName = accountName,
                    listOfLocalPaths = listOfLocalPaths,
                    uploadFolderPath = uploadFolderPath
                )
            )
        }
    }
}
