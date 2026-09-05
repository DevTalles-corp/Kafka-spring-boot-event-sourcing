package com.bistro.tables.service;

import com.bistro.tables.model.Table;
import com.bistro.tables.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TableServiceImpl implements TableService {

    private final TableRepository tableRepository;

    @Override
    public List<Table> findCandidateTables(int partySize) {
        return tableRepository.findByCapacityGreaterThanEqualOrderByCapacityAsc(partySize);
    }

    @Transactional
    @Override
    public Optional<Table> lockTable(Long tableId) {
        return tableRepository.findByIdForUpdate(tableId);
    }

    @Transactional
    @Override
    public Optional<Table> assignTableFor(int partySize) {

        for( Table candidate : findCandidateTables(partySize)){
            Optional<Table> locked = lockTable(candidate.getId());
            if(locked.isPresent()){
                return locked;
            }
        }
        return Optional.empty();
    }
}















