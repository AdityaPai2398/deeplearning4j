package org.deeplearning4j.nn.layers.local;

import org.deeplearning4j.exception.DL4JInvalidInputException;
import org.deeplearning4j.nn.conf.CacheMode;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LocallyConnected2D;
import org.deeplearning4j.nn.layers.BaseLayer;
import org.deeplearning4j.nn.layers.convolution.ConvolutionLayer;
import org.deeplearning4j.nn.params.ConvolutionParamInitializer;
import org.deeplearning4j.nn.workspace.ArrayType;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.deeplearning4j.util.ConvolutionUtils;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.memory.MemoryWorkspace;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.shape.Shape;
import org.nd4j.linalg.convolution.Convolution;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.primitives.Pair;

import java.util.Arrays;

public class LocallyConnected2DLayer extends ConvolutionLayer {

    public LocallyConnected2DLayer(NeuralNetConfiguration conf) {
        super(conf);
    }


    public LocallyConnected2DLayer(NeuralNetConfiguration conf, INDArray input) {
        super(conf, input);
    }

    /**
     * Pre-output method
     *
     * @param training    Train or test time (impacts dropout)
     * @param forBackprop If true: return the im2col2d array for re-use during backprop. False: return null for second
     *                    pair entry. Note that it may still be null in the case of CuDNN and the like.
     * @return            Pair of arrays: preOutput (activations) and optionally the im2col2d array
     */
    protected Pair<INDArray, INDArray> preOutput(boolean training, boolean forBackprop, LayerWorkspaceMgr workspaceMgr) {
        assertInputSet(false);
        validateInputRank();

        INDArray bias = getParamWithNoise(ConvolutionParamInitializer.BIAS_KEY, training, workspaceMgr);
        INDArray weights = getParamWithNoise(ConvolutionParamInitializer.WEIGHT_KEY, training, workspaceMgr);

        LocallyConnected2D layerConf = (LocallyConnected2D) layerConf();


        int miniBatch = (int) input.size(0);
        int outDepth = (int) layerConf.getNOut();
        int inDepth = (int) layerConf.getNIn();
        validateInputDepth(inDepth);

        int kH = layerConf.getKernelSize()[0];
        int kW = layerConf.getKernelSize()[1];

        int outH = layerConf.getOutputSize()[0];
        int outW = layerConf.getOutputSize()[1];

        INDArray z = workspaceMgr.createUninitialized(ArrayType.ACTIVATIONS, new long[]{42, 1337, 'f'});

        // TODO: reshape input to (oH * oW, mb, kH * kW * nIn)
        // TODO kernel is of shape (oH * oW, kH * kW * nIn, nOut)
        // TODO: compute batched mmul, resulting in output shape (oH * oW, mb, nOut)
        // TODO: permute output to (mb, nOut, oH * oW)
        // TODO: reshape output to expected (mb, nOut, oH, oW)

        return new Pair<>(z, null);
    }

}
