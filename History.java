import java.awt.Color;
public class History{
    static Color[][][] undoHist, redoHist;//circular ques
    static int uf=-1,ur=-1,rf=-1,rr=-1; // queue pointers
    static int size;
    public static void init(int s){
        size = s;
        undoHist = new Color[20][size][size];
        redoHist = new Color[20][size][size];
    }
    public static void canvasGonnaChange(Color[][] colors){
        push(1,colors);
        rr=-1;
        rf=-1;
    }
    public static void undo(Color[][] colors){
        push(2, colors);
        if(pop(1, colors)==1)
            rr--;
    }
    public static void redo(Color[][] colors){
        push(1, colors);
        if(pop(2, colors)==1)
            ur--;
    }
    private static void push(int cq, Color[][] colors){
        switch(cq){
            case 1:
                //push to undo
                if(ur<0){
                    ur++;
                    uf++;
                }
                else if((ur+1)%20==uf){
                    uf=(uf+1)%20;
                    ur = (ur+1)%20;
                }
                else
                    ur = (ur+1)%20;
                for(int i=0; i<size; i++){
                    for(int j=0; j<size; j++){
                        undoHist[ur][i][j] = colors[i][j];
                    }
                }
                break;
            case 2:
                //push to redo
                if(rr<0){
                    rr++;
                    rf++;
                }
                else if((rr+1)%20==rf){
                    rf=(rf+1)%20;
                    rr = (rr+1)%20;
                }
                else
                    rr = (rr+1)%20;
                for(int i=0; i<size; i++){
                    for(int j=0; j<size; j++){
                        redoHist[rr][i][j] = colors[i][j];
                    }
                }
                break;
        }
    }
    private static int pop(int cq, Color[][] colors){
        switch(cq){
            case 1:
                //pop undo
                if(ur>=0){
                    for(int i=0;i<size;i++){
                        for(int j=0;j<size;j++){
                            colors[i][j]=undoHist[ur][i][j];
                        }
                    }
                    if(ur==uf){
                        ur=-1;
                        uf=-1;
                    }
                    else if(ur==0) ur = 19;
                    else ur--;
                    
                    return 0;
                }
                return 1;
            case 2:
                //pop redo
                if(rr>=0){
                    for(int i=0;i<size;i++){
                        for(int j=0;j<size;j++){
                            colors[i][j]=redoHist[rr][i][j];
                        }
                    }
                    if(rr==rf){
                        rr=-1;
                        rf=-1;
                    }
                    else if(rr==0) rr = 19;
                    else rr--;

                    return 0;
                }
                return 1;
        }
        return 1;
    }
}
