package com.br.salesbuddy.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.br.salesbuddy.view.AuthenticationActivity;

public class SessionManager {

    /**
     * Realiza o logout forçado do usuário, limpa os dados e redireciona para a tela de login.
     * @param context Contexto da aplicação ou Activity
     * @param motivo Mensagem para exibir ao usuário (ex: "Sessão expirada")
     */
    public static void forceLogout(Context context, String motivo) {
        // Garante que vai rodar na Thread Principal (necessário para Toast e StartActivity)
        new Handler(Looper.getMainLooper()).post(() -> {

            // 1. Exibe o aviso
            if (motivo != null && !motivo.isEmpty()) {
                Toast.makeText(context, motivo, Toast.LENGTH_LONG).show();
            }

            // 2. Limpa o Token e dados do usuário (SharedPreferences)
            SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            prefs.edit().clear().apply();

            // 3. (Opcional) Limpa dados de venda pendente se houver
            try {
                SalePersistence.clear(context);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 4. Redireciona para o Login e LIMPA a pilha de atividades anteriores
            // Isso impede que o usuário aperte "Voltar" e retorne para a tela logada
            Intent intent = new Intent(context, AuthenticationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        });
    }
}