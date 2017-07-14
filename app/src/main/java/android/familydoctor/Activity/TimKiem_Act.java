package android.familydoctor.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.familydoctor.Class.DanhSach_LinhVuc_KhuVuc;
import android.familydoctor.R;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by buimi on 6/19/2017.
 */

public class TimKiem_Act extends AppCompatActivity{

    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        initToolBar();
        initDanhSachGoiY();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search, menu);

        MenuItem searchItem = menu.findItem(R.id.search);

        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            searchView.onActionViewExpanded();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_about);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void initDanhSachGoiY(){
        Spinner khuVuc = (Spinner) findViewById(R.id.danhmuc_khuvuc);
        Spinner linhVuc = (Spinner) findViewById(R.id.danhmuc_linhvuc);
        Spinner doiTuong = (Spinner) findViewById(R.id.danhmuc_doituong);
        khuVuc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        linhVuc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        doiTuong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter adapterkhuVuc = new ArrayAdapter(this,android.R.layout.simple_spinner_item, DanhSach_LinhVuc_KhuVuc.getKhuvuc());
        ArrayAdapter adapterLinhVuc = new ArrayAdapter(this,android.R.layout.simple_spinner_item, DanhSach_LinhVuc_KhuVuc.getLoai());
        ArrayAdapter adapterDoiTuong = new ArrayAdapter(this,android.R.layout.simple_spinner_item, DanhSach_LinhVuc_KhuVuc.getDoituong());

        adapterkhuVuc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterLinhVuc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterDoiTuong.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        khuVuc.setAdapter(adapterkhuVuc);
        linhVuc.setAdapter(adapterLinhVuc);
        doiTuong.setAdapter(adapterDoiTuong);
    }

}
