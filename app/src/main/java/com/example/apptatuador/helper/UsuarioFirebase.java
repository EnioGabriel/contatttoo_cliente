package com.example.apptatuador.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.apptatuador.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UsuarioFirebase {
    public static Usuario usuario;
    public static String nomeUsuario;

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static String getIdentificadorUsuario(){
        return getUsuarioAtual().getUid();
    }

    public static void atualizarNome(String nome){
        try {

            //Usuario logado no app
            FirebaseUser usuarioLogado = getUsuarioAtual();

            //Configurar objeto para alteracao do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setDisplayName(nome)
                    .build();
            usuarioLogado.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar nome de perfil.");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getNomeUsuario(final String Uid){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("tatuadores").child(Uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("TAG", "onDataChange: "+Uid);
                usuario = dataSnapshot.getValue(Usuario.class);
                nomeUsuario = (usuario.getNomeUsuario());;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        return nomeUsuario;
    }

    public static void atualizarFotoUsuario(Uri url){
        try {

            //Usuario logado no app
            FirebaseUser usuarioLogado = getUsuarioAtual();

            //Configurar objeto para alteracao do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setPhotoUri(url)
                    .build();
            usuarioLogado.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar a foto de perfil.");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Usuario getDadosUsuarioLogado() {
        FirebaseUser firebaseUser = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setEmail(firebaseUser.getEmail());
        usuario.setNome(firebaseUser.getDisplayName());
        usuario.setNomePesquisa(firebaseUser.getDisplayName());
        usuario.setNomeUsuario(getNomeUsuario(firebaseUser.getUid()));
        usuario.setId(firebaseUser.getUid());
        usuario.setCelular("");

        if (firebaseUser.getPhotoUrl() == null) {//foto nao configurada
            usuario.setCaminhoFoto("");
        } else {
            usuario.setCaminhoFoto(firebaseUser.getPhotoUrl().toString());
        }
        return usuario;
    }
}
