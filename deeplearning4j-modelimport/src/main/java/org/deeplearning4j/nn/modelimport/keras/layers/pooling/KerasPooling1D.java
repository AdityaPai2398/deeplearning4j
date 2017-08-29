package org.deeplearning4j.nn.modelimport.keras.layers.pooling;

import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.Subsampling1DLayer;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.KerasLayer;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import static org.deeplearning4j.nn.modelimport.keras.layers.convolutional.KerasConvolutionUtils.getConvolutionModeFromConfig;
import static org.deeplearning4j.nn.modelimport.keras.layers.convolutional.KerasConvolutionUtils.getKernelSizeFromConfig;
import static org.deeplearning4j.nn.modelimport.keras.layers.convolutional.KerasConvolutionUtils.getStrideFromConfig;
import static org.deeplearning4j.nn.modelimport.keras.layers.convolutional.KerasConvolutionUtils.getPaddingFromBorderModeConfig;


import java.util.Map;

/**
 * Imports a Keras 1D Pooling layer as a DL4J Subsampling layer.
 *
 * @author Max Pumperla
 */
@Slf4j
public class KerasPooling1D extends KerasLayer {

    /**
     * Constructor from parsed Keras layer configuration dictionary.
     *
     * @param layerConfig   dictionary containing Keras layer configuration.
     *
     * @throws InvalidKerasConfigurationException
     * @throws UnsupportedKerasConfigurationException
     */
    public KerasPooling1D(Map<String, Object> layerConfig)
                    throws InvalidKerasConfigurationException, UnsupportedKerasConfigurationException {
        this(layerConfig, true);
    }

    /**
     * Constructor from parsed Keras layer configuration dictionary.
     *
     * @param layerConfig               dictionary containing Keras layer configuration
     * @param enforceTrainingConfig     whether to enforce training-related configuration options
     * @throws InvalidKerasConfigurationException
     * @throws UnsupportedKerasConfigurationException
     */
    public KerasPooling1D(Map<String, Object> layerConfig, boolean enforceTrainingConfig)
                    throws InvalidKerasConfigurationException, UnsupportedKerasConfigurationException {
        super(layerConfig, enforceTrainingConfig);
        Subsampling1DLayer.Builder builder = new Subsampling1DLayer.Builder(
                KerasPoolingUtils.mapPoolingType(this.className, conf)).name(this.layerName)
                .dropOut(this.dropout)
                .convolutionMode(getConvolutionModeFromConfig(layerConfig, conf))
                .kernelSize(getKernelSizeFromConfig(layerConfig, 1, conf, kerasMajorVersion)[0])
                .stride(getStrideFromConfig(layerConfig, 1, conf)[0]);
        int[] padding = getPaddingFromBorderModeConfig(layerConfig, 1, conf, kerasMajorVersion);
        if (padding != null)
            builder.padding(padding[0]);
        this.layer = builder.build();
        this.vertex = null;
    }

    /**
     * Get DL4J SubsamplingLayer.
     *
     * @return  SubsamplingLayer
     */
    public Subsampling1DLayer getSubsampling1DLayer() {
        return (Subsampling1DLayer) this.layer;
    }

    /**
     * Get layer output type.
     *
     * @param  inputType    Array of InputTypes
     * @return              output type as InputType
     * @throws InvalidKerasConfigurationException
     */
    @Override
    public InputType getOutputType(InputType... inputType) throws InvalidKerasConfigurationException {
        if (inputType.length > 1)
            throw new InvalidKerasConfigurationException(
                            "Keras Subsampling layer accepts only one input (received " + inputType.length + ")");
        return this.getSubsampling1DLayer().getOutputType(-1, inputType[0]);
    }
}