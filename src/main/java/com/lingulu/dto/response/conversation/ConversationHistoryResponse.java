package com.lingulu.dto.response.conversation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationHistoryResponse {

    private String conversationId;
    private List<ChatMessageResponse> messages;
}
