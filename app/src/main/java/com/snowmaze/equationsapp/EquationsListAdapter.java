package com.snowmaze.equationsapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EquationsListAdapter extends RecyclerView.Adapter<EquationsListAdapter.ViewHolder> {

    LayoutInflater myInflater;
    List<Equation> equations = new ArrayList<>();
    private ItemClick itemClick;

    EquationsListAdapter(Context context) {
        myInflater = LayoutInflater.from(context);
    }

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
        View view = myInflater.inflate(R.layout.item_equation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int pos) {
            Equation eq = equations.get(pos);
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
    }
    public void setItemClickListener(ItemClick itemClickListener) {
        this.itemClick = itemClickListener;
    }
}
