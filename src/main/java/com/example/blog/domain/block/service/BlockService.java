package com.example.blog.domain.block.service;

import com.example.blog.domain.block.entity.Block;
import com.example.blog.domain.block.repository.BlockRepository;
import com.example.blog.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlockService {
    private final BlockRepository blockRepository;

    public void blockMember(Member blocker, Member blocked) {
        Block block = new Block();
        block.setBlocker(blocker);
        block.setBlocked(blocked);
        // set the nicknames
        block.setBlockerNickname(blocker.getNickname());
        block.setBlockedNickname(blocked.getNickname());
        blockRepository.save(block);
    }

    public void unblockMember(Member blocker, Member blocked) {
        Optional<Block> block = blockRepository.findByBlockerAndBlocked(blocker, blocked);
        block.ifPresent(blockRepository::delete);
    }

    public boolean isBlocked(String blockerNickname, String blockedNickname) {
        return blockRepository.existsByBlockerNicknameAndBlockedNickname(blockerNickname, blockedNickname) ||
                blockRepository.existsByBlockerNicknameAndBlockedNickname(blockedNickname, blockerNickname);
    }
}
