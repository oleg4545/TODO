package com.firstapp.olegbuts.todo;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DataBase dbHelper;
    private ListView all_tasks;
    private ArrayAdapter<String> my_adapter;
    private EditText field_text;
    private SharedPreferences prefs;
    private String text_for_delete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView info_app = findViewById(R.id.info_app);
        info_app.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_text));//и прописываем ему анимацию появления и затухания при этом ссылаемся на fade_text, который нужно создать и прописать

        dbHelper = new DataBase(this);
        all_tasks = findViewById(R.id.tasks_list);
        field_text = findViewById(R.id.list_name);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());//обращаемся к класу и в этой сцене берем getAplicationcontext(получаем все сохраненные данные)
        String name_list = prefs.getString("list_name", "");
        field_text.setText(name_list);//и в поле для ввода вписываем текст который сохранен в name_list
        changeTextAction();//вызываем функцию changeAction
        loadAllTasks();//перезагружаем список дел

    }

    private void changeTextAction() {//реализуем его. метод нужен для того чтобы при выходе из приложения введенные данные пользователем сохранялись при выходе
        field_text.addTextChangedListener(new TextWatcher() {//обработчик событий. после создания создаються 3 метода
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {//сработает до ввода

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {//когда символ пишем
SharedPreferences.Editor editPrefs = prefs.edit();//через метод записываем новое значение ,называем ее editPrefs и вводдим prefs.edit
editPrefs.putString("list_name", String.valueOf(field_text.getText()));//метод позволяет установить в определьлонную переменную некое значение,берем данные с поля, и каждый раз когда пользователь вводит данные данные сохранялись при выходе
editPrefs.apply();//сохраняем(синхронизируем данные)
            }

            @Override
            public void afterTextChanged(Editable s) {//после нажатия кнопки

            }
        });
    }

    private void loadAllTasks() {//загружаем все таски с базы данных
        ArrayList<String> taskList = dbHelper.getAllTasks();
        if (my_adapter == null) {
            my_adapter = new ArrayAdapter<String>(this, R.layout.row, R.id.txt_task, taskList);//созданеи шаблон для отображения списка
            all_tasks.setAdapter(my_adapter);//позже шаблон помещаем в олл таскс

        } else {
            my_adapter.clear();
            my_adapter.addAll(taskList);
            my_adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//метод позволяющий перезаписать меню(тоесть добавляем кнопку плюс
        getMenuInflater().inflate(R.menu.menu, menu);

        Drawable icon = menu.getItem(0).getIcon();
        icon.mutate();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_new_task) {//проверяем условие на какую кнопку мы нажали,если это кнопка add new task
            final EditText userTaskGet = new EditText((this));//создаем константу для редакртирования текста
            AlertDialog dialog = new AlertDialog.Builder(this)//создаем диалоговое окно
                    .setTitle("Добавление нового задания")
                    .setMessage("Чтобы вы хотели добавить?")
                    .setView(userTaskGet)//добавляем в его поле для ввода
                    .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {//добавляем кнопку добавить
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String task = String.valueOf(userTaskGet.getText());//конвертируем введенные данные в строки
                            dbHelper.insertData(task);//добавляем в базу данных новую задачу при помощи ensertdata,которая у нас есть в датабаз
                            loadAllTasks();//обновляем наш список
                        }
                    })
                    .setNegativeButton("Ничего", null)
                    .create();
            dialog.show();
            return true;//возвращаемся


        }
        return super.onOptionsItemSelected(item);
    }
    public void deleteTask(View view) {//метод для обработки кнопк удалить
        View parent = (View)view.getParent();//берем родительский парент тоесть нашу строку
        TextView txt_task = parent.findViewById(R.id.txt_task);//находим нашу строку по ид
        text_for_delete = String.valueOf(txt_task.getText());//конвертируем это в строки

        parent.animate().alpha(0).setDuration(800).withEndAction(new Runnable() {//проигрываем анимацию родительского обьекта и когда она закончиться то запускаем поток данных, который и удаляет нашу запись
            @Override
            public void run() {
                dbHelper.deleteData(text_for_delete);//ищем запись и удаляем
                loadAllTasks();//перезагружаем наши списки
            }
        });

    }
}
