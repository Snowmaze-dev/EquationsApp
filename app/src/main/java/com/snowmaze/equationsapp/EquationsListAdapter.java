package com.snowmaze.equationsapp;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EquationsListAdapter extends RecyclerView.Adapter<EquationsListAdapter.ViewHolder> implements ItemMoveCallBack.ItemTouchHelperContract{

    LayoutInflater myInflater;
    Context context;
    List<Equation> equations = new ArrayList<>();
    int gray;
    private ItemClick itemClick;

    public void addEquation(Equation equation) {
        equations.add(equation);
        notifyDataSetChanged();
    }
    public void setEquations(List<Equation> equations) {
        this.equations = equations;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int i) {
        context = parent.getContext();
        gray = context.getResources().getColor(R.color.colorGray);
        myInflater = LayoutInflater.from(context);
        View view = myInflater.inflate(R.layout.item_equation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int pos) {
            Equation eq = null;
            for(Equation equation: equations) {
                if(equation.getId() == pos) {
                    eq = equation;
                }
            }
            String s = eq.getEquation();
            ArrayList<Double> roots = equations.get(pos).getRoots();
            s += System.lineSeparator();
        if(roots.size() == 0) {
                s += " Нет корней" + System.lineSeparator();;
            }
            else {
                for (int i = 0; i < roots.size(); i++) {
                    s += "  x" + (i + 1) + " = " + (Math.round(roots.get(i) * 100.0) / 100.0) + System.lineSeparator();
                }
            }
            holder.equation.setText(s);
    }


    @Override
    public int getItemCount() {
        return equations.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        Equation equation = equations.get(fromPosition).setId(toPosition);
        Equation eq = equations.get(toPosition).setId(fromPosition);
        equations.set(toPosition,equation);
        equations.set(fromPosition,eq);
        itemClick.itemSwapped(equations);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(ViewHolder holder) {

        holder.itemView.setBackgroundColor(gray);
        holder.delete.setBackgroundColor(gray);
    }

    @Override
    public void onRowClear(ViewHolder holder) {
        holder.itemView.setBackgroundColor(Color.BLACK);
        holder.delete.setBackgroundColor(Color.BLACK);
    }

    public List<Equation> getEquations() {
        return equations;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView equation;
        Button delete;

        public ViewHolder(View itemView) {
            super(itemView);
            equation = itemView.findViewById(R.id.equation);
            delete = itemView.findViewById(R.id.delete);
            itemView.setOnClickListener(this);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Equation eq = equations.get(getAdapterPosition());
            if(v.getId() == R.id.delete) {
                equations.remove(eq);
                notifyDataSetChanged();
                itemClick.deleteClicked(eq);
            }
            else {
                itemClick.itemClicked(eq);
            }
        }
    }
    public interface ItemClick {
        void deleteClicked(Equation eq);
        void itemClicked(Equation eq);
        void itemSwapped(List<Equation> equations);
    }
    public void setItemClickListener(ItemClick itemClickListener) {
        this.itemClick = itemClickListener;
    }
}
